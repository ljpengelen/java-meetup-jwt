import { apiService } from "./services/apiService.js";
import { html, render } from 'https://unpkg.com/lit-html@1.1.2?module';
import { Page } from "./components/Page.js";

let state = {
  isLoggedIn: false,
  view: "LOG_IN",
  flash: {
    message: null,
    type: null
  },
  username: "",
  password: ""
}

const setState = newState => {
  state = {
    ...state,
    ...newState
  };

  render(
    Page({ state, setState, logIn, logOut, createAccount, updateAccount }),
    document.body
  );
}

const showFlashMessage = (message, type) => {
  setState({
    flash: {
      message,
      type
    }
  });
  setTimeout(
    () => setState({
      flash: {
        message: null,
        type: null
      }
    }),
    2000
  );
}

const logIn = () => {
  if (!state.username || state.username.trim().length == 0) {
    showFlashMessage("Username may not be blank", "danger");
  } else if (!state.password || state.password.trim().length == 0) {
    showFlashMessage("Password may not be blank", "danger");
  } else {
    apiService.logIn(state.username, state.password)
      .then(status => {
        if (status.status == "INVALID_CREDENTIALS") {
          showFlashMessage("The credentials you provided are invalid", "danger");
        } else {
          showFlashMessage("You've been logged in successfully", "success");
          setState({
            isLoggedIn: true,
            view: "UPDATE_ACCOUNT"
          });
        }
      }).catch(error => showFlashMessage(error.message, "danger"));
  }
}

const logOut = () =>
  apiService.logOut()
    .then(() =>
      setState({
        isLoggedIn: false,
        view: "LOG_IN"
      })
    ).catch(error => showFlashMessage(error.message, "danger"));

const createAccount = () => {
  if (!state.username || state.username.trim().length == 0) {
    showFlashMessage("Username may not be blank", "danger");
  } else if (!state.password || state.password.trim().length == 0) {
    showFlashMessage("Password may not be blank", "danger");
  } else {
    apiService.createAccount(state.username, state.password)
      .then(() => {
        showFlashMessage("Account created successfully", "success");
        setState({
          view: "LOG_IN"
        });
      }).catch(error => showFlashMessage(error.message, "danger"));
  }
};

const updateAccount = () => {
  if (!state.username || state.username.trim().length == 0) {
    showFlashMessage("Username may not be blank", "danger");
  } else if (!state.password || state.password.trim().length == 0) {
    showFlashMessage("Password may not be blank", "danger");
  } else {
    apiService.updateAccount(state.username, state.password)
      .then(() => showFlashMessage("Account updated successfully", "success"))
      .catch(error => showFlashMessage(error.message, "danger"));
  }
}

const getAccount = () =>
  apiService.getAccount()
    .then(account =>
      setState({
        username: account.username
      })
    ).catch(error => showFlashMessage(error.message, "danger"));

apiService.getSessionStatus()
  .then(status => {
    if (status.status == "LOGGED_IN") {
      setState({
        isLoggedIn: true,
        view: "UPDATE_ACCOUNT"
      });
      getAccount();
    } else {
      setState({
        isLoggedIn: false,
        view: "LOG_IN"
      });
    }
  }).catch(error => showFlashMessage(error.message, "danger"));
