import ComingSoonComponent from "../../../components/ComingSoonComponent"
import ButtonInput from "../../../components/form/ButtonInput"
import FormSection from "../../../components/form/FormSection"
import IpInput from "../../../components/form/IpInput"
import SelectInput from "../../../components/form/SelectInput"
import TextInput from "../../../components/form/TextInput"
import VersionInput from "../../../components/form/VersionInput"
import PageTitle from "../../../components/PageTitle"

const HostIndexPage = () => {
  return (
    <>
      <PageTitle title="Host" />
      <section className="mt-4">
        <h3 className="text-lg font-medium mb-2">Host creation</h3>
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
          <IpInput label="IpAddress" name="ipAddress" value="" placeholder="IP Address of the host" onChange={() => {}} />
          <TextInput label="Operating System" name="operatingSystem" value="" placeholder="Operating System name of the host" onChange={() => {}} />
          <SelectInput label="Patching Day" name="patchingDay" value="" placeholder="Select a patching day" onChange={() => {}} options={[]} />
          <SelectInput label="Domain" name="domain" value="" placeholder="Select a domain" onChange={() => {}} options={[{ label: "COMMON", value: "COMMON" }, { label: "MANUFACTURING", value: "MANUFACTURING" }]} />
          <SelectInput label="Network area" name="networkArea" value="" placeholder="Select a network area" onChange={() => {}} options={[{ label: "IT", value: "IT" }, { label: "DMZ", value: "DMZ" }, { label: "OT", value: "OT" }]} />
          <SelectInput label="Vlan" name="vlan" value="" placeholder="Select a vlan" onChange={() => {}} options={[]} />
          <TextInput label="Mac Address" name="macAddress" value="" placeholder="MAC address of the host" onChange={() => {}} />
          <TextInput label="Certificates" name="certificates" value="" placeholder="Certificates of the host" onChange={() => {}} />
        </FormSection>
        <ButtonInput name="create-host" label="Create Host" />
      </section>
      <ComingSoonComponent />
    </>
  )
}

export default HostIndexPage