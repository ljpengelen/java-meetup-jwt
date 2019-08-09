var app = app || {};

app.Account = Backbone.Model.extend({
  url: 'api/account',
  defaults: {
    id: null,
    username: null,
    password: null
  },
  validate: function (attributes) {
    if (!attributes.username || attributes.username.trim().length == 0) {
      return "Username may not be blank";
    }

    if (!attributes.password || attributes.password.trim().length == 0) {
      return "Password my not be blank";
    }
  }
});
