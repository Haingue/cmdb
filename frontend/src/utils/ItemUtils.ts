import { type Environment, type Host } from "../service/backend/types";
import { LinkTypeLabel } from "../service/backend/constants";
import { type ItemDto } from "../service/inventory/types";

export function convertItemIntoEnvironment(item: ItemDto): Environment {
    return {
      uuid: item.uuid!,
      projectUuid: item.incomingLinks?.find(link => link.linkType.label === LinkTypeLabel.COMPOSED_OF)?.sourceItemId,
      name: item.name,
      description: item.description,
      version: item.attributes?.find(attr => attr.label === "Revision")?.value as string || "",
      jiraTicket: item.attributes?.find(attr => attr.label === "JiraTicket")?.value as string || "",
      location: item.attributes?.find(attr => attr.label === "Location")?.value as string || "",
      type: item.attributes?.find(attr => attr.label === "Type")?.value as string || "",
      status: item.attributes?.find(attr => attr.label === "Status")?.value as string || "",
    }
}
