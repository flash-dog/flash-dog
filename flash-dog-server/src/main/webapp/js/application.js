function showTab(name) {
    var f = $$('div#content .tab-content');
    for (var i = 0; i < f.length; i++) {
        Element.hide(f[i]);
    }
    var f = $$('div.tabs a');
    for (var i = 0; i < f.length; i++) {
        Element.removeClassName(f[i], "selected");
    }
    Element.show('tab-content-' + name);
    Element.addClassName('tab-' + name, "selected");
    return false;
}