import { html } from 'https://unpkg.com/lit-html@1.1.2?module';

export const Menu = ({ isLoggedIn, showCreateAccountView, showLoginView, logOut }) => html`
  <nav class="navbar navbar-default">
    <div class="container">
      <ul class="nav navbar-nav navbar-right">
        <li>
          <a id="create-account" @click=${showCreateAccountView} href="#">
            Create account
          </a>
        </li>
        ${isLoggedIn
          ? html`<li><a @click=${logOut} href="#">Log out</a></li>`
          : html`<li><a @click=${showLoginView} href="#">Log in</a></li>`
        }
      </ul>
    </div>
  </nav>
  `;
