import { createAsyncThunk, createSlice } from '@reduxjs/toolkit'
import type { LinkTypeDto, PaginatedResponseDto } from '../service/inventory/types'
import { searchLinkTypes } from '../service/inventory/InventorySync'

export const loadLinkTypes = createAsyncThunk<{cached: boolean, data: PaginatedResponseDto<LinkTypeDto>}, void>('linkType/search', async (_, { getState, rejectWithValue }) => {
  const {linkTypes} = getState() as {linkTypes: LinkTypeState}
  if (linkTypes.lastFetched) {
      const now = Date.now()
      const fiveMinutesAgo = now - 5 * 60000
      if (linkTypes.lastFetched > fiveMinutesAgo) {
        return { cached: true, data: linkTypes.linkTypes }
      }
  }
  try {
    return { cached: false, data: await searchLinkTypes() }
  } catch (error) {
    if (error instanceof Error) return rejectWithValue(error.message)
    return rejectWithValue('An unknown error occurred')
  }
})


export type LinkTypeState = {
  linkTypes: PaginatedResponseDto<LinkTypeDto>,
  isLoading: boolean,
  error?: string,
  lastFetched?: number,
}
const initialState = {
  linkTypes: {} as PaginatedResponseDto<LinkTypeDto>,
  isLoading: true,
  error: undefined as string | undefined,
  lastFetched: null as number | null,
}
export const linkTypeSlice = createSlice({
  name: 'linkType',
  initialState,
  reducers: {
    getLinkTypes: (state) => {
      return state
    }
  },
  extraReducers: (builder) => {
    builder
    .addCase(loadLinkTypes.pending, (state) => {
      state.isLoading = true
    })
    .addCase(loadLinkTypes.fulfilled, (state, action) => {
      state.isLoading = false
      if (!action.payload.cached) {
        state.linkTypes = action.payload.data
        state.lastFetched = Date.now()
      }
      state.error = undefined
    })
    .addCase(loadLinkTypes.rejected, (state, action) => {
      console.error('Failed to fetch link types:', action.error)
      state.isLoading = false
      state.error = action.error.message
    })
  }
})

export const { getLinkTypes } = linkTypeSlice.actions

export default linkTypeSlice.reducer
