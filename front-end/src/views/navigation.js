var app = app || {};

app.NavigationView = function (options) {
  this.flashView = options.flashView;
  this.router = options.router;

  this.enableLogin = function () {
    $("#login").show();
    $("#logout").hide();
  };

  this.enableLogout = function () {
    $("#logout").show();
    $("#login").hide();
  };

  this.createAccount = function (e) {
    e.preventDefault();
    this.router.navigate("/", {trigger: true});
  };

  this.login = function (e) {
    e.preventDefault();
    this.router.navigate("/login", {trigger: true});
  };

  this.logout = function (e) {
    e.preventDefault();
    var self = this;
    $.ajax({
      url: "/api/session",
      method: "DELETE"
    }).done(function (data, textStatus, jqXHR) {
      app.token = jqXHR.getResponseHeader("X-CSRF-TOKEN");
      self.flashView.show("You've been logged out", "success");
      self.router.navigate("/", {trigger: true});
    }).fail(function (jqXHR) {
      app.token = jqXHR.getResponseHeader("X-CSRF-TOKEN");
      self.flashView.show("An unexpected error occurred while logging out", "danger");
    });
  };

  $("#create-account").click(this.createAccount.bind(this));
  $("#login").click(this.login.bind(this));
  $("#logout").click(this.logout.bind(this));
}
