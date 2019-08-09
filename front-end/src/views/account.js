var app = app || {};

app.AccountView = Backbone.View.extend({
  initialize: function (options) {
    var self = this;
    this.flashView = options.flashView;
    this.router = options.router;
    this.model.on("invalid", function (model, error) {
      self.flashView.show(error, "danger");
    });
  },
  template: _.template($('#account-template').html()),
  el: '#account-view',
  render: function () {
    this.$el.html(
      this.template(
        _.extend({action: this.action}, this.model.attributes)
      )
    );
    return this;
  },
  createAccount: function () {
    this.action = "Create account";
    this.successMessage = "Your account has been created";
  },
  editAccount: function () {
    var self = this;
    this.action = "Update account";
    this.successMessage = "Your account has been updated";
    this.model.fetch({
      success: function () {
        self.render();
      },
      error: function (model, response, options) {
        if (response.status === 401) {
          self.flashView.show("You need to login again", "danger");
          self.router.navigate("/login", {trigger: true});
        } else {
          self.flashView.show("An unexpected error occurred", "danger");
        }
      }
    });
  },
  events: {
    "change #username-field": "updateName",
    "change #password-field": "updatePassword",
    "click button": "save"
  },
  updateName: function(e) {
    this.model.set("username", e.currentTarget.value);
  },
  updatePassword: function(e) {
    this.model.set("password", e.currentTarget.value);
  },
  save: function (e) {
    e.preventDefault();
    var self = this;
    var redirectToLogin = this.model.isNew();
    this.model.save(null, {
      error: function (model, response, options) {
        var message;
        if (response.responseJSON) {
          message = response.responseJSON.message;
        } else {
          message = "An unexpected error occurred";
        }
        self.flashView.show(message, "danger");
      },
      success: function (model, response, options) {
        self.flashView.show(self.successMessage, "success");
        if (redirectToLogin === true) {
          self.router.navigate("/login", {trigger: true});
        }
      }
    })

  }
});
