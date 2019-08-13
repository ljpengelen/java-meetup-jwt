import { Credentials } from "./Credentials.js";
import { Flash } from "./Flash.js";
import { html } from 'https://unpkg.com/lit-html@1.1.2?module';
import { Menu } from "./Menu.js";

export const Page = ({ state, setState, logIn, logOut, createAccount, updateAccount }) => {
  let action;
  let handler;
  if (state.view == "LOG_IN") {
    action = "Log in";
    handler = logIn;
  } else if (state.view == "CREATE_ACCOUNT") {
    action = "Create account"
    handler = createAccount;
  } else {
    action = "Update account"
    handler = updateAccount;
  }

  return html`
    ${Menu({
      isLoggedIn: state.isLoggedIn,
      showCreateAccountView: () => setState({ view: "CREATE_ACCOUNT"}),
      showLoginView: () => setState({ view: "LOG_IN" }),
      logOut
    })}
    ${Flash(state.flash.type, state.flash.message)}
    ${Credentials({
      username: state.username,
      submitText: action,
      submitHandler: handler,
      setUsername: username => setState({ username }),
      setPassword: password => setState({ password })
    })}
  `;
}
