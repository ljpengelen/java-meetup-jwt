var app = app || {};

app.App = {
  setup: function () {
    this.router = new app.Router({
      app: this
    });

    this.flashView = new app.FlashView();

    this.navigationView = new app.NavigationView({
      flashView: this.flashView,
      router: this.router
    });

    this.account = new app.Account();
    this.accountView = new app.AccountView({
      model: this.account,
      flashView: this.flashView,
      router: this.router
    });

    this.credentials = new app.Credentials();
    this.credentialsView = new app.CredentialsView({
      model: this.credentials,
      flashView: this.flashView,
      router: this.router
    });
  },
  createAccount: function () {
    this.account.clear();
    this.account.set({username: "", password: ""});

    this.accountView.createAccount();
    this.accountView.render();
    this.credentialsView.$el.empty();
    this.navigationView.enableLogin();
  },
  editAccount: function () {
    this.flashView.show("", "");

    this.accountView.editAccount();
    this.accountView.render();
    this.credentialsView.$el.empty();
    this.navigationView.enableLogout();
  },
  loginForm: function () {
    this.credentials.set({username: "", password: ""});

    this.accountView.$el.empty();
    this.credentialsView.render();
    this.navigationView.enableLogin();
  }
};
