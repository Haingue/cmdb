import { useEffect, useState } from "react"
import { useDispatch } from "react-redux"
import { useSearchParams } from "react-router"
import ButtonInput from "../../../components/form/ButtonInput"
import FormSection from "../../../components/form/FormSection"
import IpInput from "../../../components/form/IpInput"
import SelectInput from "../../../components/form/SelectInput"
import TextInput from "../../../components/form/TextInput"
import VersionInput from "../../../components/form/VersionInput"
import PageTitle from "../../../components/PageTitle"
import HostModel from "../../../models/Host"
import { searchEnvironment } from "../../../service/backend/BackendSync"
import { DomainLabel, ItemTypeLabel, NetworkAreaLabel, PatchingDayLabel } from "../../../service/backend/constants"
import { getItemById, getItemsByIds, updateItem } from "../../../service/inventory/InventorySync"
import type { DateTime, ItemDto, LinkDto, UUID } from "../../../service/inventory/types"
import type { AppDispatch } from "../../../store"
import { addAlert } from "../../../store/alert.slice"
import AssetMap from "../../map/AssetMap"

const HostDetailsPage = () => {
  const dispatch = useDispatch<AppDispatch>()
  const [searchParams] = useSearchParams()
  const componentUuid = searchParams.get("componentUuid") as UUID | undefined

  // const [projects, setProjects] = useState<ItemDto[]>([])
  const [environments, setEnvironments] = useState<ItemDto[]>([])
  const [vlans, setVlans] = useState<ItemDto[]>([])

  const [host, setHost] = useState<HostModel>(new HostModel({} as ItemDto))

  const [environment, setEnvironment] = useState<UUID | undefined>(undefined)
  const [uuid, setUuid] = useState<UUID | undefined>(undefined)
  const [name, setName] = useState<string | undefined>(undefined)
  const [description, setDescription] = useState<string | undefined>(undefined)
  const [vlan, setVlan] = useState<string | undefined>(undefined)
  const [operatingSystem, setOperatingSystem] = useState<string | undefined>(undefined)
  const [creationTimeStamp, setCreationTimeStamp] = useState<DateTime | undefined>(undefined)
  const [type, setType] = useState<string | undefined>(undefined)
  const [version, setVersion] = useState<string | undefined>(undefined)
  const [networkArea, setNetworkArea] = useState<string | undefined>(undefined)
  const [technology, setTechnology] = useState<string | undefined>(undefined)
  const [macAddress, setMacAddress] = useState<string | undefined>(undefined)
  const [certificate, setCertificate] = useState<string | undefined>(undefined)
  const [ipAddress, setIpAddress] = useState<string | undefined>(undefined)
  const [patchingDay, setPatchingDay] = useState<string | undefined>(undefined)
  const [domain, setDomain] = useState<string | undefined>(undefined)
  const [dns, setDns] = useState<string | undefined>(undefined)
  const [communicatesWith, setCommunicatesWith] = useState<LinkDto[]>([])

  const [assetMapItems, setAssetMapItems] = useState<ItemDto[]>([])

  useEffect(() => {
    if (componentUuid) {
      getItemById(componentUuid)
        .then((item) => {
          const host: HostModel = new HostModel(item)
          setUuid(host.uuid)
          setName(host.name)
          setDescription(host.description)
          setType(host.type)
          setVersion(host.version)
          setNetworkArea(host.networkArea)
          setTechnology(host.technology)
          setMacAddress(host.macAddress)
          setCertificate(host.certificate)
          setIpAddress(host.ipAddress)
          setPatchingDay(host.patchingDay)
          setDomain(host.domain)
          setDns(host.dns)
          setCommunicatesWith(host.communicatesWith || [])
          setCreationTimeStamp(host.creationTimeStamp)

          setEnvironment(host.environmentUuid)
          setHost(host)


          setAssetMapItems([item])
          const outgoingLinks = item.outgoingLinks?.map(link => link.targetItemId) || []
          if (outgoingLinks.length > 0) {
            getItemsByIds(item.outgoingLinks?.map(link => link.targetItemId) || [])
              .then((outgoindItemDto) => {
                setAssetMapItems(prevItems => [...prevItems, ...outgoindItemDto])
              })
              .catch((error) => {
                console.error("Error fetching outgoingLinks items:", error);
                dispatch(addAlert({ type: "error", message: "Failed to fetch outgoingLinks items.", details: error }))
              })
          }
          const incomingLinks = item.incomingLinks?.map(link => link.sourceItemId) || []
          if (incomingLinks.length > 0) {
            getItemsByIds(item.incomingLinks?.map(link => link.sourceItemId) || [])
              .then((incomingItemDto) => {
                setAssetMapItems(prevItems => [...prevItems, ...incomingItemDto])
              })
              .catch((error) => {
                console.error("Error fetching incomingLinks items:", error);
                dispatch(addAlert({ type: "error", message: "Failed to fetch incomingLinks items.", details: error }))
              })
          }
        })
        .catch((error) => {
          console.error("Error fetching Host details:", error);
          dispatch(addAlert({ type: "error", message: "Failed to fetch Host details.", details: error }))
        })
    }
  }, [componentUuid])

  useEffect(() => {
    // searchProject()
    //   .then((projects) => {
    //     setProjects(projects.content)
    //   })
    //   .catch((error) => {
    //     console.error("Error fetching projects:", error);
    //     dispatch(addAlert({ type: "error", message: "Failed to fetch projects.", details: error }))
    //   })
    searchEnvironment()
      .then((environments) => {
        setEnvironments(environments.content)
      })
      .catch((error) => {
        console.error("Error fetching environments:", error);
        dispatch(addAlert({ type: "error", message: "Failed to fetch environments.", details: error }))
      })
  }, [])

  const saveHost = () => {
    console.debug("Updating host with values:", { uuid, name, description, type, version, networkArea, technology, macAddress, certificate, ipAddress, patchingDay, domain, dns })
    const _host = host
    _host.name = name
    _host.description = description
    _host.type = ItemTypeLabel[type as keyof typeof ItemTypeLabel] || type
    _host.version = version
    _host.networkArea = NetworkAreaLabel[networkArea as keyof typeof NetworkAreaLabel] || networkArea
    _host.technology = technology
    _host.macAddress = macAddress
    _host.certificate = certificate
    _host.ipAddress = ipAddress
    _host.patchingDay = PatchingDayLabel[patchingDay as keyof typeof PatchingDayLabel] || patchingDay
    _host.domain = DomainLabel[domain as keyof typeof DomainLabel] || domain
    _host.dns = dns
    setHost(_host)

    updateItem(_host.mapToItemDto())
    .then(() => {
      dispatch(addAlert({ type: "success", message: "Host updated successfully." }))
    })
    .catch((error) => {
      console.error("Error updating Host:", error);
      dispatch(addAlert({ type: "error", message: "Failed to update Host.", details: error }))
    })
  }

  return (
    <>
      <PageTitle title="Host" />
      <section className="mt-4">
        <h3 className="text-lg font-medium mb-2">Host creation</h3>
        <FormSection title="Parent">
          <SelectInput label="Environment" name="environment" value={environment as string} placeholder="Select an environment" onChange={(e) => setEnvironment(e.target.value)} options={environments.map((env) => ({ label: env.name!, value: env.uuid as string }))} />
        </FormSection>
        <FormSection title="Identifiers">
          <TextInput label="Name" name="label" value={name} placeholder="Name of the software" onChange={(e) => setName(e.target.value)} />
          <TextInput label="Description" name="description" value={description} placeholder="Description of the software" onChange={(e) => setDescription(e.target.value)} />
        </FormSection>
        <FormSection title="Attributes">
          <VersionInput label="Version" name="version" value={version} placeholder="Version of the business service" onChange={(e) => setVersion(e.target.value)} />
          <SelectInput label="Technology" name="technology" value={technology} placeholder="Select a technology" onChange={(e) => setTechnology(e.target.value)} options={[]} />
          <IpInput label="IpAddress" name="ipAddress" value={ipAddress} placeholder="IP Address of the host" onChange={(e) => setIpAddress(e.target.value)} />
          <TextInput label="Operating System" name="operatingSystem" value={operatingSystem} placeholder="Operating System name of the host" onChange={(e) => setOperatingSystem(e.target.value)} />
          <SelectInput label="Patching Day" name="patchingDay" value={patchingDay} placeholder="Select a patching day" onChange={(e) => setPatchingDay(e.target.value)} options={Object.keys(PatchingDayLabel).map(key => ({ label: key, value: key })) } />
          <SelectInput label="Domain" name="domain" value={domain} placeholder="Select a domain" onChange={(e) => setDomain(e.target.value)} options={Object.keys(DomainLabel).map(key => ({ label: key, value: key })) } />
          <SelectInput label="Network area" name="networkArea" value={networkArea} placeholder="Select a network area" onChange={(e) => setNetworkArea(e.target.value)} options={Object.keys(NetworkAreaLabel).map(key => ({ label: key, value: key })) } />
          <SelectInput label="Vlan" name="vlan" value={vlan} placeholder="Select a vlan" onChange={(e) => setVlan(e.target.value)} options={vlans.map(itemDto => ({ label: itemDto.name!, value: itemDto.uuid as string })) } />
          <TextInput label="Mac Address" name="macAddress" value={macAddress} placeholder="MAC address of the host" onChange={(e) => setMacAddress(e.target.value)} />
          <TextInput label="Certificates" name="certificates" value={certificate} placeholder="Certificates of the host" onChange={(e) => setCertificate(e.target.value)} />
          <TextInput label="DNS" name="dns" value={dns} placeholder="DNS of the host" onChange={(e) => setDns(e.target.value)} />
        </FormSection>
        <ButtonInput name="update-host" label="Update Host" onClick={saveHost} />
      </section>
      <section className="mt-4 h-[30vh] border border-gray-300 rounded-lg mt-4">
        <AssetMap items={assetMapItems} />
      </section>
    </>
  )
}

export default HostDetailsPage