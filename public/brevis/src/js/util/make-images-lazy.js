import { brevisFetch as fetch } from './brevis-fetch';

let parser = new DOMParser();

export function makeImagesLazy(htmlStr) {
    let doc = parser.parseFromString(htmlStr, "text/html");

    let imgs = doc.querySelectorAll('img');

    for (let i = 0; i < imgs.length; i++) {
        let img = imgs[i];

        let src = img.src;
        img.src='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAAAAAA6fptVAAAACklEQVR4nGP6BgAA/AD52XJK6gAAAABJRU5ErkJggg==';
        img.setAttribute('data-src', src);
    }

    return doc.documentElement.innerHTML;
}

export function fetchImages(el) {
    let imgs = el.querySelectorAll('img');

    for (let i = 0; i < imgs.length; i++) {
        let img = imgs[i];

        fetch(img.getAttribute('data-src'), {}, 10000)
            .then((res) => {
                return res.blob();
            })
            .then((blob) => {
                let objectURL = URL.createObjectURL(blob);
                img.src = objectURL;
            })
    }
}