import type { HTTPHeaders } from "@/api/raumreservierung-backend";

import { getHeaders } from "@/api/fetch-utils.ts";
import {
  BaseAPI,
  Configuration,
  TheEntityControllerApi,
} from "@/api/raumreservierung-backend";

type ApiCtor<T extends BaseAPI> = new (config: Configuration) => T;

export class ApiFactory {
  private readonly __brand!: never;

  private static instances = new Map<ApiCtor<BaseAPI>, BaseAPI>();
  private static entityInstance: TheEntityControllerApi;

  static getInstance<T extends BaseAPI>(ApiClass: ApiCtor<T>): T {
    const existing = this.instances.get(ApiClass);
    if (existing) {
      console.log(existing);
      return existing as T;
    }

    const config = new Configuration({
      basePath: "/api/backend-service",
      credentials: "same-origin",
      headers: convertHeaders(getHeaders()),
    });

    const api = new ApiClass(config);
    this.instances.set(ApiClass, api);
    console.log(api);
    return api;
  }

  // #######################
  static getEntityInstance() {
    if (!ApiFactory.entityInstance) {
      const config = new Configuration({
        basePath: "/api/backend-service",
        credentials: "same-origin",
        headers: convertHeaders(getHeaders()),
      });
      ApiFactory.entityInstance = new TheEntityControllerApi(config);
    }
    return ApiFactory.entityInstance;
  }
}

/**
 * Converts a Headers object into a simple key-value pair object.
 * @param {Headers} headers - The headers object to be converted.
 * @returns {HTTPHeaders} An object with the same headers.
 */
function convertHeaders(headers: Headers): HTTPHeaders {
  const httpHeaders: HTTPHeaders = {};

  headers.forEach((value, key) => {
    httpHeaders[key] = value;
  });

  return httpHeaders;
}
