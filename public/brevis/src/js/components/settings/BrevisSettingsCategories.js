import Inferno from 'inferno';
import Component from 'inferno-component';

class BrevisSettingsCategories extends Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {

    }

    render() {
        let domCategories = this.props.categories.sort((a, b) => {
            if (a.count > b.count ) { return -1 } else { return 1 }
        }).map((category) => {
            return (
                <div className="brevis-settings-categories brevis-settings-category">
                    <div className="brevis-settings-categories brevis-settings-category-name">{category.name}</div>
                    <div className="brevis-settings-categories brevis-settings-category-count">{category.count}</div>
                </div>
            )
        });

        return (
            <div className="brevis-settings-categories">
                <div className="brevis-settings-categories brevis-settings-categories-description">Brevis recommends articles based on this log of categories you tap into the most.</div>
                <div className="brevis-settings-categories brevis-settings-categories-list">
                    {domCategories}
                </div>
            </div>
        )
    }
}

export default BrevisSettingsCategories