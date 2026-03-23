export interface TableHeader<T> {
  title: string;
  value: Extract<keyof T, string> | (string & {});
  align?: "start" | "center" | "end";
  sortable?: boolean;
  width?: string | number;
}
