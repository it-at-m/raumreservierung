import type { SeatingTypeResponseDto } from "@/api/raumreservierung-backend";

export interface SeatingTypeWithCapacity extends SeatingTypeResponseDto {
  capacity: number;
}
