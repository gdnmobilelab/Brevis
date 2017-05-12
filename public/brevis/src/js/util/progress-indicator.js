
export default function progressIndicator(total, cb) {
    let succeeded = 0,
        failed = 0;

    return (success) => {
        if (success) {
            succeeded += 1;
        } else {
            failed += 1;
        }

        cb(succeeded, failed);
    }
}