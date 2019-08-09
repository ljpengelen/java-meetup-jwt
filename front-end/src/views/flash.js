var app = app || {};

app.FlashView = Backbone.View.extend({
  template: _.template($('#flash-template').html()),
  el: '#flash-view',
  render: function () {
    this.$el.html(this.template({message: this.message, type: this.type}));
    return this;
  },
  message: "",
  type: "",
  show: function (message, type) {
    this.message = message;
    this.type = type;
    this.render();
  }
});
