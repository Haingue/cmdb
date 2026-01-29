import { useState } from "react"
import ComingSoonComponent from "../../components/ComingSoonComponent"
import ButtonInput from "../../components/form/ButtonInput"
import FormSection from "../../components/form/FormSection"
import TextInput from "../../components/form/TextInput"
import PageTitle from "../../components/PageTitle"
import { createBusinessService } from "../../service/backend/BackendSync"
import { useDispatch } from "react-redux"
import type { AppDispatch } from "../../store"
import { addAlert } from "../../store/alert.slice"

const BusinessServiceIndexPage = () => {
  const dispatch = useDispatch<AppDispatch>()
  const [abbreviation, setAbbreviation] = useState("")
  const [name, setName] = useState("")
  const [description, setDescription] = useState("")

  const clearForm = () => {
    setAbbreviation("")
    setName("")
    setDescription("")
  }

  const validForm = () => {
    createBusinessService({ uuid: null, abbreviation, name  })
    .then((item) => {
      console.debug("Business Service created:", item);
      clearForm()
      dispatch(addAlert({ type: "success", message: "Business Service created successfully." }))
    })
    .catch((error) => {
      console.error("Error creating Business Service:", error);
      dispatch(addAlert({ type: "error", message: "Failed to create Business Service.", details: error }))
    });
  }

  return (
    <>
      <PageTitle title="Business Services" />
      <section className="mt-4">
        <h3 className="text-lg font-medium mb-2">Business Services creation</h3>
        <FormSection title="Identifiers">
          <TextInput label="Abbreviation" name="abbreviation" value={abbreviation} placeholder="Abbreviation of the business service" onChange={(e) => setAbbreviation(e.target.value)} />
          <TextInput label="Name" name="name" value={name} placeholder="Name of the business service" onChange={(e) => setName(e.target.value)} />
          <TextInput label="Description" name="description" value={description} placeholder="Description of the business service" onChange={(e) => setDescription(e.target.value)} disabled />
        </FormSection>
        <ButtonInput name="create-business-service" label="Create Business Service" onClick={validForm} />
      </section>
      <ComingSoonComponent />
    </>
  )
}

export default BusinessServiceIndexPage