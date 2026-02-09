import { createApp } from "vue";

import App from "@/App.vue";
import { registerPlugins } from "@/plugins";

import "unfonts.css";

import { registerDirectives } from "@/directives";

const app = createApp(App);

registerPlugins(app);
registerDirectives(app);

app.mount("#app");
