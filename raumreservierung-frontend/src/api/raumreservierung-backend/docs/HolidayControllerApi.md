# HolidayControllerApi

All URIs are relative to *http://localhost:39146*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createHoliday**](HolidayControllerApi.md#createholiday) | **POST** /holidays |  |
| [**deleteHoliday**](HolidayControllerApi.md#deleteholiday) | **DELETE** /holidays/{id} |  |
| [**getPublicHolidays**](HolidayControllerApi.md#getpublicholidays) | **GET** /holidays/public |  |
| [**getSchoolHolidays**](HolidayControllerApi.md#getschoolholidays) | **GET** /holidays/school |  |
| [**updateHoliday**](HolidayControllerApi.md#updateholiday) | **PUT** /holidays |  |



## createHoliday

> HolidayResponseDTO createHoliday(holidayRequestDTO)



### Example

```ts
import {
  Configuration,
  HolidayControllerApi,
} from '';
import type { CreateHolidayRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new HolidayControllerApi();

  const body = {
    // HolidayRequestDTO
    holidayRequestDTO: ...,
  } satisfies CreateHolidayRequest;

  try {
    const data = await api.createHoliday(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **holidayRequestDTO** | [HolidayRequestDTO](HolidayRequestDTO.md) |  | |

### Return type

[**HolidayResponseDTO**](HolidayResponseDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `*/*`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## deleteHoliday

> deleteHoliday(id)



### Example

```ts
import {
  Configuration,
  HolidayControllerApi,
} from '';
import type { DeleteHolidayRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new HolidayControllerApi();

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies DeleteHolidayRequest;

  try {
    const data = await api.deleteHoliday(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | `string` |  | [Defaults to `undefined`] |

### Return type

`void` (Empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getPublicHolidays

> Array&lt;HolidayResponseDTO&gt; getPublicHolidays()



### Example

```ts
import {
  Configuration,
  HolidayControllerApi,
} from '';
import type { GetPublicHolidaysRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new HolidayControllerApi();

  try {
    const data = await api.getPublicHolidays();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**Array&lt;HolidayResponseDTO&gt;**](HolidayResponseDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `*/*`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getSchoolHolidays

> Array&lt;HolidayResponseDTO&gt; getSchoolHolidays()



### Example

```ts
import {
  Configuration,
  HolidayControllerApi,
} from '';
import type { GetSchoolHolidaysRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new HolidayControllerApi();

  try {
    const data = await api.getSchoolHolidays();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**Array&lt;HolidayResponseDTO&gt;**](HolidayResponseDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `*/*`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateHoliday

> HolidayResponseDTO updateHoliday(holidayRequestDTO)



### Example

```ts
import {
  Configuration,
  HolidayControllerApi,
} from '';
import type { UpdateHolidayRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new HolidayControllerApi();

  const body = {
    // HolidayRequestDTO
    holidayRequestDTO: ...,
  } satisfies UpdateHolidayRequest;

  try {
    const data = await api.updateHoliday(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **holidayRequestDTO** | [HolidayRequestDTO](HolidayRequestDTO.md) |  | |

### Return type

[**HolidayResponseDTO**](HolidayResponseDTO.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `*/*`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

