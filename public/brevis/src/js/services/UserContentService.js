import APIService from './APIService';

class UserContentService {
    updateContentsMeta(contentMetas) {
        return Promise.all(contentMetas.map((contentMeta) => {
            return this.updateContentMeta(contentMeta)
                .then((success) => {
                    return {
                        success: true,
                        id: contentMeta.contentId
                    }
                })
                .catch((err) => {
                    return {
                        success: false,
                        id: contentMeta.contentId
                    }
                })
        }))
    }

    updateContentMeta(contentMeta) {
        return APIService
            .post('/api/user/content/meta', {
                contentId: contentMeta.contentId,
                meta: contentMeta.meta
            })
            .catch((err) => {
                console.log(`There was an error updating the content meta: ${err}`);
                throw err;
            })
    }

    fetchContent() {
        return APIService
            .get('/api/user/content')
            .catch((err) => {
                console.log(`There was an error getting the content: ${err}`);
                throw err;
            })
    }
}

export default new UserContentService()