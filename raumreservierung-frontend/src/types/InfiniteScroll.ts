export interface InfiniteScrollLoad {
  side: "start" | "end" | "both";
  done: (status: "ok" | "empty" | "error") => void;
}
