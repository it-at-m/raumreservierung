type RuleValue = string | number | null | undefined;

export const useRules = () => ({
  required: (msg: string) => (value: RuleValue) => !!value || msg,

  minLength: (min: number, msg: string) => (value: RuleValue) =>
    !value || String(value).length >= min || msg,

  maxLength: (max: number, msg: string) => (value: RuleValue) =>
    !value || String(value).length <= max || msg,
});
