var app = app || {};

app.Router = Backbone.Router.extend({
  initialize: function (options) {
    this.app = options.app;
  },
  routes: {
    "account": "editAccount",
    "login": "loginForm",
    "*path": "createAccount"
  },
  createAccount: function (path) {
    if (path) {
      this.navigate('/');
    } else {
      this.app.createAccount();
    }
  },
  loginForm: function () {
    this.app.loginForm();
  },
  editAccount: function () {
    this.app.editAccount();
  }
});
