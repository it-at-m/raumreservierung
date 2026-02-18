# EquipmentControllerApi

All URIs are relative to *http://localhost:39146*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**deleteEquipment**](EquipmentControllerApi.md#deleteequipment) | **DELETE** /equipment | Delete an equipment entity. |
| [**getAllEquipments**](EquipmentControllerApi.md#getallequipments) | **GET** /equipment | Retrieve all equipment entities. |
| [**saveEquipment**](EquipmentControllerApi.md#saveequipment) | **POST** /equipment | Create a new equipment entity. |
| [**updateEquipment**](EquipmentControllerApi.md#updateequipment) | **PUT** /equipment | Update an existing equipment entity. |



## deleteEquipment

> deleteEquipment(body)

Delete an equipment entity.

Delete an equipment entity.  Deletes the equipment resource identified by the given UUID.

### Example

```ts
import {
  Configuration,
  EquipmentControllerApi,
} from '';
import type { DeleteEquipmentRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new EquipmentControllerApi();

  const body = {
    // string | the UUID of the equipment to delete
    body: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies DeleteEquipmentRequest;

  try {
    const data = await api.deleteEquipment(body);
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
| **body** | `string` | the UUID of the equipment to delete | |

### Return type

`void` (Empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OK |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getAllEquipments

> Array&lt;EquipmentResponseDto&gt; getAllEquipments()

Retrieve all equipment entities.

Retrieve all equipment entities.  Returns a list of all existing equipment resources.

### Example

```ts
import {
  Configuration,
  EquipmentControllerApi,
} from '';
import type { GetAllEquipmentsRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new EquipmentControllerApi();

  try {
    const data = await api.getAllEquipments();
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

[**Array&lt;EquipmentResponseDto&gt;**](EquipmentResponseDto.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `*/*`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | list of equipment as response DTOs |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## saveEquipment

> EquipmentResponseDto saveEquipment(equipmentRequestDto)

Create a new equipment entity.

Create a new equipment entity.  Creates a new equipment resource using the provided details.

### Example

```ts
import {
  Configuration,
  EquipmentControllerApi,
} from '';
import type { SaveEquipmentRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new EquipmentControllerApi();

  const body = {
    // EquipmentRequestDto | the details of the equipment to create
    equipmentRequestDto: ...,
  } satisfies SaveEquipmentRequest;

  try {
    const data = await api.saveEquipment(body);
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
| **equipmentRequestDto** | [EquipmentRequestDto](EquipmentRequestDto.md) | the details of the equipment to create | |

### Return type

[**EquipmentResponseDto**](EquipmentResponseDto.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `*/*`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | the created equipment as response DTO |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateEquipment

> EquipmentResponseDto updateEquipment(equipmentRequestDto)

Update an existing equipment entity.

Update an existing equipment entity.  Updates an existing equipment resource using the provided details.

### Example

```ts
import {
  Configuration,
  EquipmentControllerApi,
} from '';
import type { UpdateEquipmentRequest } from '';

async function example() {
  console.log("🚀 Testing  SDK...");
  const api = new EquipmentControllerApi();

  const body = {
    // EquipmentRequestDto | the updated equipment details
    equipmentRequestDto: ...,
  } satisfies UpdateEquipmentRequest;

  try {
    const data = await api.updateEquipment(body);
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
| **equipmentRequestDto** | [EquipmentRequestDto](EquipmentRequestDto.md) | the updated equipment details | |

### Return type

[**EquipmentResponseDto**](EquipmentResponseDto.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `*/*`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | the updated equipment as response DTO |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

