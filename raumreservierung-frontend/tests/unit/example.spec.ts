import { shallowMount } from "@vue/test-utils";
import { createPinia } from "pinia";
import { describe, expect, test } from "vitest";
import { defineComponent, nextTick } from "vue";

import TheSnackbarQueue from "../../src/components/TheSnackbarQueue.vue";
import { useSnackbarStore } from "../../src/stores/snackbar";

describe("TheSnackbarQueue.vue", () => {
  test("renders message from pinia snackbar store queue", async () => {
    const pinia = createPinia();

    const VSnackbarQueueStub = defineComponent({
      name: "VSnackbarQueueStub",
      props: {
        modelValue: {
          type: Array,
          default: () => [],
        },
      },
      emits: ["update:modelValue"],
      template: `
        <div>
          <div v-for="(item, idx) in modelValue" :key="idx">
            <slot name="text" :item="item" />
          </div>
        </div>
      `,
    });

    const wrapper = shallowMount(TheSnackbarQueue, {
      global: {
        plugins: [pinia],
        stubs: {
          "v-snackbar-queue": VSnackbarQueueStub,
          "v-layout": { template: "<div><slot /></div>" },
          "v-icon": { template: "<i />" },
        },
      },
    });

    const store = useSnackbarStore(pinia);

    const message = "Hello_World";
    store.add({ message }); // level/timeout/icon optional -> Defaults greifen

    await nextTick();

    expect(wrapper.text()).toContain(message);
  });
});
