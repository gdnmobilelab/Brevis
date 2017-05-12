package auth;

import akka.util.ByteString;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.inject.Inject;
import db.BriefDAO;
import db.ContentDAO;
import db.UserContentRecommendationDAO;
import db.UserDAO;
import models.BrevisBrief;
import models.BrevisContent;
import models.BrevisUser;
import models.BrevisUserAccountType;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.http.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.oauth.profile.google2.Google2ProfileDefinition;
import org.pac4j.play.CallbackController;
import org.pac4j.play.PlayWebContext;
import play.http.HttpEntity;
import play.mvc.Result;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import static com.github.scribejava.core.model.OAuthConstants.CLIENT_ID;
import static org.pac4j.core.util.CommonHelper.*;

import scala.collection.mutable.ArrayBuffer;
import scala.compat.java8.OptionConverters;
import services.BrevisUserContentRecommendationCreator;

/**
 * Created by connor.jennings on 3/30/17.
 *
 * Don't try this at home kids.
 */
public class GoogleLoginCallbackController extends CallbackController {
    private final String ONBOARDING_URL = "/brevis/app/#/onboarding";
    private final String DEFAULT_REDIRECT_RUL = "/brevis/app/";

    private UserDAO userDAO;
    private ContentDAO contentDAO;
    private BriefDAO briefDAO;
    private BrevisUserContentRecommendationCreator brevisUserContentRecommendationCreator;
    private GoogleIdTokenVerifier verifier;
    private ObjectMapper objectMapper;

    @Inject
    public GoogleLoginCallbackController(UserDAO userDAO,
                                         ContentDAO contentDAO,
                                         BriefDAO briefDAO,
                                         BrevisUserContentRecommendationCreator brevisUserContentRecommendationCreator) {
        this.userDAO = userDAO;
        this.contentDAO = contentDAO;
        this.briefDAO = briefDAO;
        this.brevisUserContentRecommendationCreator = brevisUserContentRecommendationCreator;

        List<String> ids = new LinkedList<String>();
        ids.add("261418409017-dj5vc2th59htqadml4et2v176r9bgqn6.apps.googleusercontent.com");
        ids.add("1024640553453-oij2a58sth7mipc0qqgstmicgilorei0.apps.googleusercontent.com");
        ids.add("536578476667-kq87tagk6r5c11de7l0sr1dpgo7ljos8.apps.googleusercontent.com");

        this.verifier = new GoogleIdTokenVerifier.Builder(new ApacheHttpTransport(), new JacksonFactory())
                .setAudience(ids)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public CompletionStage<Result> callback() {
        final PlayWebContext playWebContext = new PlayWebContext(ctx(), playSessionStore);

        return CompletableFuture.supplyAsync(() -> perform(playWebContext, config, config.getHttpActionAdapter(), this.getDefaultUrl(), true, true), ec.current());
    }

    public CompletionStage<Result> validate() {
        final PlayWebContext playWebContext = new PlayWebContext(ctx(), playSessionStore);

        return CompletableFuture.supplyAsync(() -> validate(playWebContext, config, config.getHttpActionAdapter(), this.getDefaultUrl(), true, true), ec.current());
    }

    @SuppressWarnings("Duplicates")
    private Result validate(final PlayWebContext context, final Config config, final HttpActionAdapter<Result, PlayWebContext> httpActionAdapter,
                           final String inputDefaultUrl, final Boolean inputMultiProfile, final Boolean inputRenewSession) {

        final boolean multiProfile = false;
        final boolean renewSession = true;

        Map<String, Object> respBody = new HashMap<>();
        int status;

        try {
            String token = context.getJavaContext().request().body().asJson().get("token").asText();
            GoogleIdToken idToken = this.verifier.verify(token);

            if (idToken == null) {
                throw new Exception("Invalid token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            ObjectMapper objMapper = new ObjectMapper();
            Google2ProfileDefinition googleProfile = new Google2ProfileDefinition();
            payload.set("id", "org.pac4j.oauth.profile.google2.Google2Profile#" + payload.getSubject());

            CommonProfile profile = googleProfile.extractUserProfile(objMapper.writeValueAsString(payload));

            boolean userExists = userDAO.getUserByExternalId(profile.getTypedId()).isDefined();

            if (!userExists) {
                BrevisUser newBrevisUser = userDAO.createBrevisUser(BrevisUser.apply(
                        UUID.randomUUID().toString(),
                        profile.getTypedId(),
                        BrevisUserAccountType.GOOGLE(),
                        profile.getEmail(),
                        30,
                        30,
                        LocalTime.of(8, 30)));

                Optional<BrevisBrief> activeBrief = OptionConverters.toJava(briefDAO.getActiveBrief());

                activeBrief.ifPresent(brevisBrief -> brevisUserContentRecommendationCreator.saveRecommendedContentToUser(
                        brevisUserContentRecommendationCreator.createScoredContentForBrief(
                                newBrevisUser.id(),
                                brevisBrief.id(),
                                30,
                                contentDAO.getContentForActiveBrief()),
                        newBrevisUser
                ));

                saveUserProfile(context, config, profile, multiProfile, renewSession);
            } else {
                saveUserProfile(context, config, profile, multiProfile, renewSession);
            }

            status = 200;
            respBody.put("sessionId", context.getSessionStore().getTrackableSession(context));
        } catch (Exception e) {
            respBody.put("failed", true);
            status = 400;
        }

        try {
            return new Result(status, new HttpEntity.Strict(ByteString.fromString(this.objectMapper.writeValueAsString(respBody), "utf8"), Optional.of("application/json")));
        } catch (JsonProcessingException e1) {
            return new Result(400, new HttpEntity.Strict(ByteString.fromString("{\"failed\":true}", "utf8"), Optional.of("application/json")));
        }
    }

    private Result perform(final PlayWebContext context, final Config config, final HttpActionAdapter<Result, PlayWebContext> httpActionAdapter,
                     final String inputDefaultUrl, final Boolean inputMultiProfile, final Boolean inputRenewSession) {

        final PlayWebContext playWebContext = new PlayWebContext(ctx(), playSessionStore);

        // default values
        final String defaultUrl;
        if (this.getDefaultUrl() == null) {
            defaultUrl = Pac4jConstants.DEFAULT_URL_VALUE;
        } else {
            defaultUrl = this.getDefaultUrl();
        }

        final boolean multiProfile = false;
        final boolean renewSession = true;

        // checks
        assertNotNull("context", playWebContext);
        assertNotNull("config", config);
        assertNotNull("httpActionAdapter", config.getHttpActionAdapter());
        assertNotBlank(Pac4jConstants.DEFAULT_URL, defaultUrl);
        final Clients clients = config.getClients();
        assertNotNull("clients", clients);

        // logic
        final Client client = clients.findClient(playWebContext);
        assertNotNull("client", client);
        assertTrue(client instanceof IndirectClient, "only indirect clients are allowed on the callback url");

        HttpAction action;
        try {
            final Credentials credentials = client.getCredentials(playWebContext);

            final CommonProfile profile = client.getUserProfile(credentials, playWebContext);

            if (profile == null) {
                HttpAction.redirect("redirect", context, DEFAULT_REDIRECT_RUL);
                return httpActionAdapter.adapt(302, context);
            }

            assertNotNull("profile", profile);

            if (!profile.getEmail().endsWith("guardian.co.uk")
                    && !profile.getEmail().endsWith("dylangreif.com")
                    && !profile.getEmail().endsWith("maassmedia.com")) {
                return httpActionAdapter.adapt(403, context);
            }

            //Todo: need to check if profile is not null

            // Todo: very important for security, don't forget!!!!!!!
            // Todo: verify the integrity of the token, see https://developers.google.com/identity/sign-in/web/backend-auth
            boolean userExists = userDAO.getUserByExternalId(profile.getTypedId()).isDefined();

            if (!userExists) {
                BrevisUser newBrevisUser = userDAO.createBrevisUser(BrevisUser.apply(
                        UUID.randomUUID().toString(),
                        profile.getTypedId(),
                        BrevisUserAccountType.GOOGLE(),
                        profile.getEmail(),
                        30,
                        30,
                        LocalTime.of(8, 30)));

                Optional<BrevisBrief> activeBrief = OptionConverters.toJava(briefDAO.getActiveBrief());

                activeBrief.ifPresent(brevisBrief -> brevisUserContentRecommendationCreator.saveRecommendedContentToUser(
                        brevisUserContentRecommendationCreator.createScoredContentForBrief(
                                newBrevisUser.id(),
                                brevisBrief.id(),
                                30,
                                contentDAO.getContentForActiveBrief()),
                        newBrevisUser
                ));

                saveUserProfile(playWebContext, config, profile, multiProfile, renewSession);
                action = redirectToOriginallyRequestedUrlOrNewUrl(playWebContext, ONBOARDING_URL);
            } else {
                saveUserProfile(playWebContext, config, profile, multiProfile, renewSession);
                action = redirectToOriginallyRequestedUrlOrNewUrl(playWebContext, DEFAULT_REDIRECT_RUL);
            }

        } catch (final HttpAction e) {
            action = e;
        }

        return httpActionAdapter.adapt(action.getCode(), context);
    }

    private void saveUserProfile(final PlayWebContext context, final Config config, final CommonProfile profile,
                                   final boolean multiProfile, final boolean renewSession) {
        final ProfileManager manager = new ProfileManager(context);
        if (profile != null) {
            manager.save(true, profile, multiProfile);
            if (renewSession) {
                renewSession(context, config);
            }
        }
    }

    private void renewSession(final PlayWebContext context, final Config config) {
        final SessionStore<PlayWebContext> sessionStore = context.getSessionStore();
        if (sessionStore != null) {
            final String oldSessionId = sessionStore.getOrCreateSessionId(context);
            final boolean renewed = sessionStore.renewSession(context);
            if (renewed) {
                final String newSessionId = sessionStore.getOrCreateSessionId(context);
                final Clients clients = config.getClients();
                if (clients != null) {
                    final List<Client> clientList = clients.getClients();
                    for (final Client client : clientList) {
                        final BaseClient baseClient = (BaseClient) client;
                        baseClient.notifySessionRenewal(oldSessionId, context);
                    }
                }
            } else {
                // Todo: log something here
            }
        } else {
            // todo: log something here
        }
    }


    protected HttpAction redirectToOriginallyRequestedUrlOrNewUrl(final PlayWebContext context, final String newUrl) {
        final String requestedUrl = (String) context.getSessionAttribute(Pac4jConstants.REQUESTED_URL);

        String urlToRedirect;
        if (isNotBlank(requestedUrl) && newUrl == null) {
            urlToRedirect = requestedUrl;
        } else if (newUrl != null) {
            urlToRedirect = newUrl;
        } else {
            urlToRedirect = DEFAULT_REDIRECT_RUL;
        }

        return HttpAction.redirect("redirect", context, urlToRedirect);

    }
}
