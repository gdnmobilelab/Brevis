import APIService from './APIService';

class UserRecommendationsService {
    fetchRecommendations() {
        return APIService
            .get('/api/user/recommendations')
            .catch((err) => {
                console.log(`There was an error getting the content: ${err}`);
                throw err;
            })
    }
}

export default new UserRecommendationsService()