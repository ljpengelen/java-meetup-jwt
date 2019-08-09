var app = app || {};

app.CredentialsView = Backbone.View.extend({
  initialize: function (options) {
    var self = this;
    this.flashView = options.flashView;
    this.router = options.router;
    this.model.on("invalid", function (model, error) {
      self.flashView.show(error, "danger");
    });
  },
  template: _.template($('#credentials-template').html()),
  el: '#credentials-view',
  render: function () {
    this.$el.html(this.template(this.model.attributes));
    return this;
  },
  events: {
    "change #username-field": "updateName",
    "change #password-field": "updatePassword",
    "click button": "login"
  },
  updateName: function(e) {
    this.model.set("username", e.currentTarget.value);
  },
  updatePassword: function(e) {
    this.model.set("password", e.currentTarget.value);
  },
  login: function (e) {
    e.preventDefault();
    if (this.model.isValid()) {
      var self = this;
      $.ajax({
        url: "/api/session",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify(this.model.toJSON())
      }).done(function (data) {
        if (data.status == "INVALID_CREDENTIALS") {
          self.flashView.show("The credentials you provided are invalid", "danger");
        } else {
          self.router.navigate("/account", {trigger: true});
        }
      }).fail(function () {
        self.flashView.show("An unexpected error occurred while logging in", "danger");
      });
    }
  }
});
