Element.prototype.addClass = function(className) {
    return this.classList.add(className);
};

Element.prototype.removeClass = function(className) {
    return this.classList.remove(className);
};

Element.prototype.toggleClass = function(className) {
    return this.classList.toggle(className);
};