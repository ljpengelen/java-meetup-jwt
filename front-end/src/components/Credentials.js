import { html } from 'https://unpkg.com/lit-html@1.1.2?module';

export const Credentials = ({ username, setUsername, setPassword, submitText, submitHandler }) => html`
<div class="container">
  <form class="credentials-form">
    <input
      autocomplete="username"
      type="text"
      class="form-control"
      placeholder="Username"
      @change=${e => setUsername(e.target.value)}
      value="${username}"
    />
    <input
      autocomplete="off"
      type="password"
      class="form-control"
      @change=${e => setPassword(e.target.value)}
      placeholder="Password"
    />
    <button
      class="btn btn-primary btn-block"
      @click=${e => {e.preventDefault(); submitHandler()}}
    >
      ${submitText}
    </button>
  </form>
</div>
`
