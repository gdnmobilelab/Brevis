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
            .post(`/api/user/content/${contentMeta.contentId}/meta`, contentMeta.meta)
            .catch((err) => {
                console.log(`There was an error updating the content meta: ${err}`);
                throw err;
            })
    }

    findClickedContent() {
        return APIService
            .get('/api/user/content/clicked')
            .catch((err) => {
                console.log(`There was an error fetching the clicked content: ${err}`);
                throw err;
            })
    }
}

export default new UserContentService()