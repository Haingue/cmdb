import { createAsyncThunk, createSlice } from '@reduxjs/toolkit'
import type { ItemTypeDto, PaginatedResponseDto } from '../service/inventory/types'
import { searchItemTypes } from '../service/inventory/InventorySync'

export const loadItemTypes = createAsyncThunk('itemType/search', async (_, { getState, rejectWithValue }) => {
  const {itemTypes} = getState() as {itemTypes: ItemTypeState}
  if (itemTypes.lastFetched) {
      const now = Date.now();
      const fiveMinutesAgo = now - 5 * 60000;
      if (itemTypes.lastFetched > fiveMinutesAgo) {
        return { cached: true, data: itemTypes.itemTypes };
      }
  }
  try {
    return { cached: false, data: await searchItemTypes() }
  } catch (error) {
    if (error instanceof Error) return rejectWithValue(error.message)
    return rejectWithValue('An unknown error occurred')
  }
})


export type ItemTypeState = {
  itemTypes: PaginatedResponseDto<ItemTypeDto>,
  isLoading: boolean,
  error?: string,
  lastFetched?: number,
}
const initialState = {
  itemTypes: {} as PaginatedResponseDto<ItemTypeDto>,
  isLoading: true,
  error: undefined as string | undefined,
  lastFetched: null as number | null,
}
export const itemTypeSlice = createSlice({
  name: 'itemType',
  initialState,
  reducers: {
    getItemTypes: (state) => {
      return state
    }
  },
  extraReducers: (builder) => {
    builder
    .addCase(loadItemTypes.pending, (state) => {
      state.isLoading = true
    })
    .addCase(loadItemTypes.fulfilled, (state, action) => {
      state.isLoading = false
      if (!action.payload.cached) {
        state.itemTypes = action.payload.data
        state.lastFetched = Date.now()
      }
      state.error = undefined
    })
    .addCase(loadItemTypes.rejected, (state, action) => {
      console.error('Failed to fetch item types:', action.error)
      state.isLoading = false
      state.error = action.error.message
    })
  }
})

export const { getItemTypes } = itemTypeSlice.actions

export default itemTypeSlice.reducer
