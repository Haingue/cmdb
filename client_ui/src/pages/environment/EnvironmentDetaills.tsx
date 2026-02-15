import { useEffect, useState } from "react"
import ComingSoonComponent from "../../components/ComingSoonComponent"
import ButtonInput from "../../components/form/ButtonInput"
import SelectInput from "../../components/form/SelectInput"
import TextInput from "../../components/form/TextInput"
import VersionInput from "../../components/form/VersionInput"
import PageTitle from "../../components/PageTitle"
import FormSection from "../../components/form/FormSection"
import { searchItems, searchItemTypes } from "../../service/inventory/InventorySync"
import type { AppDispatch } from "../../store"
import { useDispatch } from "react-redux"
import { addAlert } from "../../store/alert.slice"
import SimpleTable from "../../components/table-simple"
import type { ItemDto, ItemTypeDto } from "../../service/inventory/types"
import type { Project } from "../../service/backend/types"
import { searchProject } from "../../service/backend/BackendSync"

const EnvironmentDetaillsPage = () => {
  const dispatch = useDispatch<AppDispatch>()
  const [projects, setProjects] = useState<Project[]>([])
  const [environmentItemType, setEnvironmentItemType] = useState<ItemTypeDto>({})
  const [environments, setEnvironments] = useState<ItemDto[]>([])

  const [project, setProject] = useState({
    uuid: '',
    shortname: '',
  })
  const [type, setType] = useState('Acceptance')
  const [name, setName] = useState(`${project?.shortname || 'Project'}-${type}`)

  useEffect(() => {
    searchItemTypes("Environment")
    .then((_itemTypes) => {
      console.debug("Environment Item Type fetched:", _itemTypes)
      setEnvironmentItemType(_itemTypes.content[0])
    })
    .catch((error) => {
      console.error("Error fetching Environment Item Type:", error);
      dispatch(addAlert({ type: "error", message: "Failed to fetch Environment Item Type.", details: error }))
    })
    searchItems(undefined, "Environment")
    .then((_environments) => {
      console.debug("Environments fetched:", _environments);
      setEnvironments(_environments.content)
    })
    .catch((error) => {
      console.error("Error fetching Environments:", error);
      dispatch(addAlert({ type: "error", message: "Failed to fetch Environments.", details: error }))
    })

    searchProject()
    .then((_projects) => {
      console.debug("Projects fetched:", _projects);
      setProjects(_projects.content.filter(item => item?.uuid !== null).map((item) => ({
        uuid: item.uuid || "",
        name: item.name || "Unnamed Project",
        abbreviation: item.attributes?.find(attr => attr.label === "abbreviation")?.value as string || "N/A"
      })))
    })
    .then(() => {
      console.debug("Projects set in state:", projects);
    })
    .catch((error) => {
      console.error("Error fetching Projects:", error);
      dispatch(addAlert({ type: "error", message: "Failed to fetch Projects.", details: error }))
    });
  }, [])

  return (
    <>
      <PageTitle title="Environments" />
      <section className="mt-4">
        <h3 className="text-lg font-medium mb-2">Environment creation</h3>
        <FormSection title="Parent">
          <SelectInput label="Project" name="project" value={project.uuid} placeholder="Select a project" onChange={(e) => setProject({uuid: e.target.value, shortname: projects.find(p => p.uuid === e.target.value)?.shortName || ''})} options={projects.map(p => ({label: p.name!, value: `${p.uuid}`}))} />
        </FormSection>
        <FormSection title="Identifiers">
          <TextInput label="Name" name="name" value={name} placeholder="Name of the environment" onChange={() => {}} readonly />
          <TextInput label="Description" name="description" value={`${name} is the ${type} environnement`} placeholder="Description of the environment" onChange={() => {}} readonly />
        </FormSection>
        <FormSection title="Attributes">
          <SelectInput label="Type" name="type" value="" placeholder="Select a type" onChange={(e) => setType(e.target.value)} options={[{label: "Development", value: "Development"}, {label: "Acceptance", value: "Acceptance"}, {label: "Production", value: "Production"}]} />
          <SelectInput label="Status" name="status" value="" placeholder="Select a status" onChange={() => {}} options={[{label: "REQUESTED", value: "REQUESTED"}, {label: "IN_PROGRESS", value: "IN_PROGRESS"}, {label: "READY", value: "READY"}, {label: "DEPLOYED", value: "DEPLOYED"}, {label: "STOPPED", value: "STOPPED"}, {label: "DECOMMISSIONED", value: "DECOMMISSIONED"}]} />
          <VersionInput label="Version" name="version" value="" placeholder="Version of the business service" onChange={() => {}} />
          <TextInput label="JiraTicket" name="jiraTicket" value={name} placeholder="Number of the JiraTicket this environment setup" onChange={() => {}} />
          <SelectInput label="Location" name="location" value="" placeholder="Select a location" onChange={() => {}} options={[{label: "Plant 1", value: "plant_1"}, {label: "Plant 2", value: "plant_2"}]} />
        </FormSection>
        <ButtonInput name="create-environment" label="Create Environment" />
      </section>
      <section className="mt-4">
        <h3 className="text-xl-heading font-medium mb-2">Childs</h3>
        <ComingSoonComponent />
      </section>
      <section className="mt-4">
        <h3 className="text-xl-heading font-medium mb-2">Linked items</h3>
        <ComingSoonComponent />
      </section>
    </>
  )
}

export default EnvironmentDetaillsPage