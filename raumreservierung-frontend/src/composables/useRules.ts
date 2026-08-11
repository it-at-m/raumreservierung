type RuleValue = string | number | null | undefined | unknown[];

export const useRules = () => ({
  required: (msg: string) => (value: RuleValue) => {
    if (Array.isArray(value)) {
      return value.length > 0 || msg;
    }
    return !!value || msg;
  },

  minLength: (min: number, msg: string) => (value: RuleValue) =>
    !value || String(value).length >= min || msg,

  maxLength: (max: number, msg: string) => (value: RuleValue) =>
    !value || String(value).length <= max || msg,
});
