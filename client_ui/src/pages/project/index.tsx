import { useDispatch } from "react-redux"
import ComingSoonComponent from "../../components/ComingSoonComponent"
import ButtonInput from "../../components/form/ButtonInput"
import FormSection from "../../components/form/FormSection"
import SelectInput from "../../components/form/SelectInput"
import TextInput from "../../components/form/TextInput"
import VersionInput from "../../components/form/VersionInput"
import PageTitle from "../../components/PageTitle"
import type { AppDispatch } from "../../store"
import { useEffect, useState } from "react"
import { createProject, searchBusinessService } from "../../service/backend/BackendSync"
import { addAlert } from "../../store/alert.slice"
import { type BusinessService } from "../../service/backend/types"

const ProjectIndexPage = () => {
  const dispatch = useDispatch<AppDispatch>()
  const [businessServices, setBusinessServices] = useState<BusinessService[]>([])

  const [name, setName] = useState("")
  const [description, setDescription] = useState("")
  const [fullname, setFullname] = useState("")
  const [shortname, setShortname] = useState("")
  const [version, setVersion] = useState("")
  const [business_service, setBusiness_service] = useState("")
  const [owners, setOwners] = useState("")
  const [maintainers, setMaintainers] = useState("")
  
  useEffect(() => {
    searchBusinessService()
    .then((_businessServices) => {
      console.debug("Business Services fetched:", _businessServices);
      setBusinessServices(_businessServices.content.filter(item => item?.uuid !== null).map((item) => ({
        uuid: item.uuid || "",
        name: item.name || "Unnamed Business Service",
        abbreviation: item.attributes?.find(attr => attr.label === "abbreviation")?.value as string || "N/A"
      })))
    })
    .catch((error) => {
      console.error("Error fetching Business Services:", error);
      dispatch(addAlert({ type: "error", message: "Failed to fetch Business Services.", details: error }))
    });

    // TODO: fetch owners and maintainers groups
  }, [])

  const clearForm = () => {
    setName("")
    setDescription("")
    setFullname("")
    setShortname("")
    setVersion("")
    setBusiness_service("")
    setOwners("")
    setMaintainers("")
  }

  const validForm = () => {
    const businessService = businessServices.find(bs => bs.uuid === business_service)
    if (!businessService) {
      dispatch(addAlert({ type: "error", message: "Invalid Business Service selected." }))
      return
    }
    const project = {
      uuid: null,
      name,
      description,
      fullname,
      shortname,
      version,
      businessService,
      owners,
      maintainers
    }
    createProject({
      uuid: null,
      requestor: null,
      requestTimestamp: new Date().toISOString(),
      project,
      businessService
    })
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
          <TextInput label="Full name" name="fullname" value={fullname} placeholder="Full name of the project" onChange={(e) => setFullname(e.target.value)} />
          <TextInput label="Short name" name="shortname" value={shortname} placeholder="Short name of the project, used to prefix component" onChange={(e) => setShortname(e.target.value)} />
          <VersionInput label="Version" name="version" value={version} placeholder="Version of the business service" onChange={() => {}} />
          <SelectInput label="Business Service" name="business_service" value={business_service} placeholder="Select a business service" onChange={(e) => setBusiness_service(e.target.value)} options={businessServices.map((bs) => ({ label: bs.name, value: `${bs.uuid}` }))} />
          <SelectInput label="Owners" name="owners" value={owners} placeholder="Select an group of the project owners" onChange={() => {}} options={[]} />
          <SelectInput label="Maintainers" name="maintainers" value={maintainers} placeholder="Select an group of the project maintainers" onChange={() => {}} options={[]} />
        </FormSection>
        <ButtonInput name="create-project" label="Create Project" onClick={validForm} />
      </section>
      <ComingSoonComponent />
    </>
  )
}

export default ProjectIndexPage