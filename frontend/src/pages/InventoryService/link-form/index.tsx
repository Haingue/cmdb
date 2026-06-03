import { useEffect, useState } from 'react'
import { useDispatch } from 'react-redux'
import { useSearchParams } from 'react-router'
import PageTitle from '../../../components/PageTitle'
import ButtonInput from '../../../components/form/ButtonInput'
import FormSection from '../../../components/form/FormSection'
import SelectInput from '../../../components/form/SelectInput'
import TextAreaInput from '../../../components/form/TextAreaInput'
import { createLink, searchItems, searchItemTypes, searchLinkTypes } from '../../../service/inventory/InventorySync'
import type { ItemDto, ItemTypeDto, LinkTypeDto, UUID } from '../../../service/inventory/types'
import type { AppDispatch } from '../../../store'
import { addAlert } from '../../../store/alert.slice'

const LinkForm = () => {
  const dispatch = useDispatch<AppDispatch>()
  const [searchParams] = useSearchParams()
  const initialSourceItemUuid = searchParams.get("sourceItemUuid") as UUID | undefined

  const [linkTypes, setLinkTypes] = useState<LinkTypeDto[]>([])
  const [itemTypes, setItemTypes] = useState<ItemTypeDto[]>([])
  const [sourceItems, setSourceItems] = useState<ItemDto[]>([])
  const [targetItems, setTargetItems] = useState<ItemDto[]>([])

  const [source, setSource] = useState<ItemDto | undefined>(undefined)
  const [sourceItemType, setSourceItemType] = useState<ItemTypeDto | undefined>(undefined)
  const [sourceNamePattern, setSourceNamePattern] = useState<string | undefined>(undefined)
  const [target, setTarget] = useState<ItemDto | undefined>(undefined)
  const [targetItemType, setTargetItemType] = useState<ItemTypeDto | undefined>(undefined)
  const [targetNamePattern, setTargetNamePattern] = useState<string | undefined>(undefined)
  const [linkType, setLinkType] = useState<LinkTypeDto | undefined>(undefined)
  const [description, setDescription] = useState<string | undefined>(undefined)

  useEffect(() => {
    searchItemTypes()
    .then((itemTypes) => {
      setItemTypes(itemTypes.content)
    }).catch((error) => {
      dispatch(addAlert({ type: "error", message: "Error fetching item types.", details: error }))
    })
    searchLinkTypes()
    .then((linkTypes) => {
      setLinkTypes(linkTypes.content)
    }).catch((error) => {
      dispatch(addAlert({ type: "error", message: "Error fetching link types.", details: error }))
    })
  }, [])

  const selectItemType = (itemTypeUuid: UUID | null, isSource: boolean) => {
    const itemType = itemTypes.find((t) => t.uuid === itemTypeUuid)
    if (isSource) {
      setSourceItemType(itemType)
      setSource(undefined)
      setSourceNamePattern(undefined)
      if (itemType) {
        searchItems({ itemType: itemType.label })
          .then((items) => {
            setSourceItems(items.content)
          }).catch((error) => {
            dispatch(addAlert({ type: "error", message: "Error fetching source items.", details: error }))
          })
      } else {
        setSourceItems([])
      }
    } else {
      setTargetItemType(itemType)
      setTarget(undefined)
      setTargetNamePattern(undefined)
      if (itemType) {
        searchItems({ itemType: itemType.label })
          .then((items) => {
            setTargetItems(items.content)
          }).catch((error) => {
            console.error("Error fetching target items:", error)
          })
      } else {
        setTargetItems([])
      }
    }
  }

  const selectItem = (itemUuid: UUID | null, isSource: boolean) => {
    const item = (isSource ? sourceItems : targetItems).find((i) => i.uuid === itemUuid)
    if (isSource) {
      setSource(item)
    } else {
      setTarget(item)
    }
  }

  const validForm = () => {
    console.assert(!source, "Source item must be selected")
    console.assert(!target, "Target item must be selected")
    console.assert(!linkType, "Link type must be selected")
    if (source && target && linkType) {
      createLink({
        sourceItemId: source.uuid!,
        sourceItemName: source.name!,
        targetItemId: target.uuid!,
        targetItemName: target.name!,
        linkType: linkType!,
        description,
      }).then(() => {
        dispatch(addAlert({ type: "success", message: "Link created successfully." }))
      }).catch((error) => {
        dispatch(addAlert({ type: "error", message: "Failed to create link.", details: error }))
      })
    }
  }

  return (
    <>
      <PageTitle title="Asset links" />
      <section className="h-[25vh] border border-gray-300 rounded-lg mt-4">
          <svg className="h-full w-full" viewBox="0 0 300 100" preserveAspectRatio="xMinYMin meet">
            <g transform="translate(0, 0)">

              {source && (
                <>
                  {/* Source item */}
                  <text x="50" y="40" className="text-sm font-semibold fill-gray-800">
                    {source.name}
                  </text>
                  <text x="50" y="60" className="text-xs fill-gray-600">
                    {sourceItemType?.label}
                  </text>
                </>
              )}

              { source && target && (
                <>
                  {/* Animated arrow */}
                  <defs>
                    <marker id="arrowhead" markerWidth="10" markerHeight="10" refX="9" refY="3" orient="auto">
                      <polygon points="0 0, 10 3, 0 6" fill="#666" />
                    </marker>
                  </defs>
                  <line x1="150" y1="50" x2="250" y2="50" stroke="#666" strokeWidth="2" markerEnd="url(#arrowhead)" strokeDasharray="5,5">
                    <animate attributeName="stroke-dashoffset" from="0" to="-10" dur="0.5s" repeatCount="indefinite" />
                  </line>
                  <text x="200" y="40" className="text-xs fill-gray-700" textAnchor="middle">
                    {linkType?.label || 'link'}
                  </text>
                </>
              )}
                  
              {target && (
                <>
                  {/* Target item */}
                  <text x="280" y="40" className="text-sm font-semibold fill-gray-800">
                    {target.name}
                  </text>
                  <text x="280" y="60" className="text-xs fill-gray-600">
                    {targetItemType?.label}
                  </text>
                </>
              )}
            </g>
          </svg>
      </section>
      <FormSection title="Source">
        <SelectInput label="Item type" name="sourceItemType"
          value={sourceItemType?.uuid as string}
          placeholder="Select an item type"
          onChange={(e) => selectItemType(e.target.value as UUID, true)}
          options={itemTypes.map((t) => ({ label: t.label, value: t.uuid as string }))}
          disabled={!!initialSourceItemUuid}
          />
        <SelectInput label="Item" name="sourceItemType"
          value={source?.uuid as string}
          placeholder="Select an item"
          onChange={(e) => selectItem(e.target.value as UUID, true)}
          options={sourceItems.map((t) => ({ label: t.name!, value: t.uuid as string }))}
          disabled={!!initialSourceItemUuid}
          />
      </FormSection>
      <FormSection title="Target">
        <SelectInput label="Item type" name="targetItemType"
          value={targetItemType?.uuid as string}
          placeholder="Select an item type"
          onChange={(e) => selectItemType(e.target.value as UUID, false)}
          options={itemTypes.map((t) => ({ label: t.label, value: t.uuid as string }))}
          />
        <SelectInput label="Item" name="targetItemType"
          value={target?.uuid as string}
          placeholder="Select an item"
          onChange={(e) => selectItem(e.target.value as UUID, false)}
          options={targetItems.map((t) => ({ label: t.name!, value: t.uuid as string }))}
          disabled={!!initialSourceItemUuid}
          />
      </FormSection>
      <FormSection title="New link">
        <SelectInput label="Link type" name="linkType"
          value={linkType?.uuid as string}
          placeholder="Select a link type"
          onChange={(e) => setLinkType(linkTypes.find((t) => t.uuid === e.target.value))}
          options={linkTypes.map((t) => ({ label: t.label, value: t.uuid as string }))}
          />
        <TextAreaInput
          label="Description"
          name="linkDescription"
          value={description || ''}
          maxlength={64}
          onChange={(e) => setDescription(e.target.value)} disabled={!!initialSourceItemUuid}
          />
      </FormSection>
      <ButtonInput name="link-items" label="Connect Items" onClick={validForm} />
    </>
  )
}

export default LinkForm