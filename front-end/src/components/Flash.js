import { html } from 'https://unpkg.com/lit-html@1.1.2?module';

export const Flash = (type, message) =>
message ?
  html`
    <div class="container">
      <div class="alert alert-${type}">
        ${message}
      </div>
    </div>
  ` : "";
