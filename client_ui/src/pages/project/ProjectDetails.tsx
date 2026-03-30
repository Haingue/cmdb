import { useEffect, useState } from "react"
import { useDispatch } from "react-redux"
import { useNavigate, useSearchParams } from "react-router"
import ComingSoonComponent from "../../components/ComingSoonComponent"
import ButtonInput from "../../components/form/ButtonInput"
import FormSection from "../../components/form/FormSection"
import SelectInput from "../../components/form/SelectInput"
import TextInput from "../../components/form/TextInput"
import VersionInput from "../../components/form/VersionInput"
import PageTitle from "../../components/PageTitle"
import { createProject, searchBusinessService } from "../../service/backend/BackendSync"
import { type UserGroup, type BusinessService, type Project, type ProjectCreationRequest } from "../../service/backend/types"
import { getItemById, searchItemTypes } from "../../service/inventory/InventorySync"
import type { ItemTypeDto, UUID } from "../../service/inventory/types"
import type { AppDispatch } from "../../store"
import { addAlert } from "../../store/alert.slice"

const ProjectDetailsPage = () => {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams();
  const projectUuid = searchParams.get("projectUuid") as UUID | undefined
  const isNewProject: boolean = !projectUuid

  const dispatch = useDispatch<AppDispatch>()
  const [businessServices, setBusinessServices] = useState<BusinessService[]>([])
  const [projectItemType, setProjectItemType] = useState<ItemTypeDto>({} as ItemTypeDto)

  const [name, setName] = useState("")
  const [description, setDescription] = useState("")
  const [fullName, setFullName] = useState("")
  const [shortName, setShortName] = useState("")
  const [version, setVersion] = useState("")
  const [businessServiceName, setBusinessServiceName] = useState<string | undefined>(undefined)
  const [owners, setOwners] = useState<UserGroup | undefined>(undefined)
  const [maintainers, setMaintainers] = useState<UserGroup | undefined>(undefined)
  
  useEffect(() => {
    searchItemTypes("Project")
    .then((_itemTypes) => {
      console.debug("Project Item Type fetched:", _itemTypes)
      setProjectItemType(_itemTypes.content[0])
    })
    .catch((error) => {
      console.error("Error fetching Project Item Type:", error);
      dispatch(addAlert({ type: "error", message: "Failed to fetch Project Item Type.", details: error }))
    })
    searchBusinessService()
    .then((_businessServices) => {
      console.debug("Business Services fetched:", _businessServices);
      setBusinessServices(_businessServices.content.filter(item => item?.uuid !== null).map((item) => ({
        uuid: item.uuid || "",
        name: item.name || "Unnamed Business Service",
        abbreviation: item.attributes?.find(attr => attr.label === "Abbreviation")?.value as string || "N/A"
      })))
    })
    .then(() => {
      console.debug("Business Services set in state:", businessServices);
    })
    .catch((error) => {
      console.error("Error fetching Business Services:", error);
      dispatch(addAlert({ type: "error", message: "Failed to fetch Business Services.", details: error }))
    });

    // TODO: fetch owners and maintainers groups
  }, [])

  useEffect(() => {
    if (projectUuid) {
      console.debug("Displaying project details for UUID:", projectUuid);
      getItemById(projectUuid)
      .then((item) => {
        console.debug("Project fetched by UUID:", item)
        // TODO set inputs
        setName(item.name || "")
        setDescription(item.description || "")
        setFullName(item.attributes?.find(attr => attr.label === "FullName")?.value as string || "")
        setShortName(item.attributes?.find(attr => attr.label === "ShortName")?.value as string || "")
        setVersion(item.attributes?.find(attr => attr.label === "version")?.value as string || "")
        setBusinessServiceName(businessServices.find(bs => bs.uuid === item.attributes?.find(attr => attr.label === "businessServiceUuid")?.value)?.name || undefined)
        setOwners(item.attributes?.find(attr => attr.label === "owners")?.value as UserGroup || undefined)
        setMaintainers(item.attributes?.find(attr => attr.label === "maintainers")?.value as UserGroup || undefined)
      })
      .catch((error) => {
        console.error("Error fetching Project by UUID:", error);
        dispatch(addAlert({ type: "error", message: "Failed to fetch Project by UUID.", details: error }))
      })
    } else {
      console.debug("No project UUID provided, displaying project creation form.");
    }
  }, [projectUuid])

  const clearForm = () => {
    setName("")
    setDescription("")
    setFullName("")
    setShortName("")
    setVersion("")
    setBusinessServiceName(undefined)
    setOwners(undefined)
    setMaintainers(undefined)
  }

  const validForm = () => {
    const businessService = businessServices.find(bs => bs.name === businessServiceName)
    if (!businessService) {
      dispatch(addAlert({ type: "error", message: "Invalid Business Service selected." }))
      return
    }
    const project: Project = {
      uuid: projectUuid || null,
      name,
      description,
      fullName: fullName,
      shortName: shortName,
      version,
      businessServiceName: businessService.name,
      owners,
      maintainers,
    }
    const projectCreationRequest: ProjectCreationRequest = {
      uuid: null,
      requestor: null,
      requestTimestamp: new Date().toISOString(),
      project,
      businessService
    }
    createProject(projectCreationRequest)
    .then((item) => {
      console.debug("Project created:", item);
      clearForm()
      dispatch(addAlert({ type: "success", message: "Project created successfully." }))
    })
    .catch((error) => {
      console.error("Error creating Project:", error);
      dispatch(addAlert({ type: "error", message: "Failed to create Project.", details: error }))
    });
  }

  return (
    <>
      <PageTitle title="Projects" />
      <section className="mt-4">
        <h3 className="text-lg font-medium mb-2">Project creation</h3>
        <FormSection title="Identifiers">
          <TextInput label="Name" name="name" value={name} placeholder="Name of the software" onChange={(e) => setName(e.target.value)} />
          <TextInput label="Description" name="description" value={description} placeholder="Description of the software" onChange={(e) => setDescription(e.target.value)} />
        </FormSection>
        <FormSection title="Attributes">
          <TextInput label="Full name" name="fullname" value={fullName} placeholder="Full name of the project" onChange={(e) => setFullName(e.target.value)} />
          <TextInput label="Short name" name="shortname" value={shortName} placeholder="Short name of the project, used to prefix component" onChange={(e) => setShortName(e.target.value)} />
          <VersionInput label="Version" name="version" value={version} placeholder="Version of the business service" onChange={(e) => setVersion(e.target.value)} />
          <SelectInput label="Business Service" name="business_service" value={businessServiceName} placeholder="Select a business service" onChange={(e) => setBusinessServiceName(e.target.value)} options={businessServices.map((bs) => ({ label: bs.name, value: `${bs.name}` }))} />
          <SelectInput label="Owners" name="owners" value={owners?.name} placeholder="Select an group of the project owners" onChange={() => {}} options={[]} />
          <SelectInput label="Maintainers" name="maintainers" value={maintainers?.name} placeholder="Select an group of the project maintainers" onChange={() => {}} options={[]} />
        </FormSection>
        { isNewProject && <ButtonInput name="create-project" label="Create Project" onClick={validForm} /> }
        { !isNewProject && <ButtonInput name="update-project" label="Update Project" onClick={validForm} /> }
      </section>
      <section className="mt-4">
        <h3 className="text-xl-heading font-medium mb-2">Childs</h3>
        <ButtonInput name="add-new-environment" label="Add new Environment" onClick={() => navigate(`/environment-details?projectUuid=${projectUuid}`)} />
        <ComingSoonComponent />
      </section>
      <section className="mt-4">
        <h3 className="text-xl-heading font-medium mb-2">Linked items</h3>
        <ComingSoonComponent />
      </section>
    </>
  )
}

export default ProjectDetailsPage