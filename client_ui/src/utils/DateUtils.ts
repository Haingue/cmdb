import type { DateTime } from "../service/inventory/types";

export function dateFromTimestamp(timestamp: DateTime): Date {
    return new Date(`${timestamp[0]}-${timestamp[1]}-${timestamp[2]} ${timestamp[3]}:${timestamp[4]}:${timestamp[5]}.${timestamp[6]}`)
}