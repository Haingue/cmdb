import { useEffect, useState } from "react"
import ButtonInput from "../../components/form/ButtonInput"
import FormSection from "../../components/form/FormSection"
import TextInput from "../../components/form/TextInput"
import PageTitle from "../../components/PageTitle"
import { createBusinessService } from "../../service/backend/BackendSync"
import { useDispatch } from "react-redux"
import type { AppDispatch } from "../../store"
import { addAlert } from "../../store/alert.slice"
import SimpleTable from "../../components/table-simple"
import { type ItemDto, type ItemTypeDto } from "../../service/inventory/types"
import { searchItems, searchItemTypes } from "../../service/inventory/InventorySync"

const BusinessServiceIndexPage = () => {
  const dispatch = useDispatch<AppDispatch>()
  const [businessServiceItemType, setBusinessServiceItemType] = useState<ItemTypeDto>({} as ItemTypeDto)
  const [businessServices, setBusinessServices] = useState<ItemDto[]>([])

  const [abbreviation, setAbbreviation] = useState("")
  const [name, setName] = useState("")
  const [description, setDescription] = useState("")

  useEffect(() => {
    searchItemTypes("BusinessService")
      .then((_itemTypes) => {
        console.debug("BusinessService Item Type fetched:", _itemTypes)
        setBusinessServiceItemType(_itemTypes.content[0])
      })
      .catch((error) => {
        console.error("Error fetching BusinessService Item Type:", error);
        dispatch(addAlert({ type: "error", message: "Failed to fetch BusinessService Item Type.", details: error }))
      })
      searchItems(undefined, "BusinessService")
      .then((_businessServices) => {
        console.debug("BusinessServices fetched:", _businessServices);
        setBusinessServices(_businessServices.content)
      })
      .catch((error) => {
        console.error("Error fetching BusinessServices:", error);
        dispatch(addAlert({ type: "error", message: "Failed to fetch BusinessServices.", details: error }))
      })
  }, [])

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
          <TextInput label="Abbreviation" name="abbreviation" value={abbreviation} maxlength={3} placeholder="Abbreviation of the business service" onChange={(e) => setAbbreviation(e.target.value)} />
          <TextInput label="Name" name="name" value={name} placeholder="Name of the business service" onChange={(e) => setName(e.target.value)} />
          <TextInput label="Description" name="description" value={description} placeholder="Description of the business service" onChange={(e) => setDescription(e.target.value)} disabled />
        </FormSection>
        <ButtonInput name="create-business-service" label="Create Business Service" onClick={validForm} />
      </section>
      <section className="mt-4">
        <h3 className="text-xl-heading font-medium mb-2">Existing Projects</h3>
        <SimpleTable
          columns={[
            { name: 'uuid', label: 'uuid' },
            { name: 'name', label: 'name' },
            ...(businessServiceItemType && businessServiceItemType?.attributes?.map(attr => ({ name: attr.label, label: attr.label }))) || [],
            { name: 'lastModifiedDate', label: 'lastModifiedDate' },
          ]}
          rows={businessServices?.map(item => ({
            uuid: {content: item.uuid},
            name: {content: item.name},
            ...item.attributes && { ...item.attributes.reduce((acc, attr) => ({ ...acc, [attr.label]: { content: attr.value } }), {}) },
            lastModifiedDate: {content: item.lastModifiedDate},
          })) || []}
          isCollapsed={false}
        />
      </section>
    </>
  )
}

export default BusinessServiceIndexPage