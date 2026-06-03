import ComingSoonComponent from "../../../components/ComingSoonComponent"
import ButtonInput from "../../../components/form/ButtonInput"
import FormSection from "../../../components/form/FormSection"
import SelectInput from "../../../components/form/SelectInput"
import TextInput from "../../../components/form/TextInput"
import VersionInput from "../../../components/form/VersionInput"
import PageTitle from "../../../components/PageTitle"

const SoftwareDetailsPage = () => {
  return (
    <>
      <PageTitle title="Software detail" />
      <section className="mt-4">
        <h3 className="text-lg font-medium mb-2">Software creation</h3>
        <FormSection title="Parent">
          <SelectInput label="Environment" name="environment" value="" placeholder="Select an environment" onChange={() => {}} options={[]} />
        </FormSection>
        <FormSection title="Identifiers">
          <TextInput label="Name" name="label" value="" placeholder="Name of the software" onChange={() => {}} />
          <TextInput label="Description" name="description" value="" placeholder="Description of the software" onChange={() => {}} />
        </FormSection>
        <FormSection title="Attributes">
          <VersionInput label="Version" name="version" value="" placeholder="Version of the business service" onChange={() => {}} />
          <SelectInput label="Technology" name="technology" value="" placeholder="Select a technology" onChange={() => {}} options={[]} />
          <TextInput label="Certificates" name="certificates" value="" placeholder="Certificates of the host" onChange={() => {}} />
          <TextInput label="Git repository" name="gitRepository" value="" placeholder="Name of the Git repository" onChange={() => {}} />
        </FormSection>
        <ButtonInput name="create-software" label="Create Software" />
      </section>
      <ComingSoonComponent />
    </>
  )
}

export default SoftwareDetailsPage