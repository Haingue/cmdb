import { createAsyncThunk, createSlice } from '@reduxjs/toolkit'
import type { ItemDto, PaginatedResponseDto } from '../service/inventory/types'
import { searchItems } from '../service/inventory/InventorySync'

export const loadItems = createAsyncThunk('item/search', async (_, { getState, rejectWithValue }) => {
  const {items} = getState() as {items: ItemState}
  if (items.lastFetched) {
      const now = Date.now();
      const fiveMinutesAgo = now - 5 * 60000;
      if (items.lastFetched > fiveMinutesAgo) {
        return { cached: true, data: items.items };
      }
  }
  try {
    return { cached: false, data: await searchItems() }
  } catch (error) {
    if (error instanceof Error) return rejectWithValue(error.message)
    return rejectWithValue('An unknown error occurred')
  }
})

export type ItemState = {
  items: PaginatedResponseDto<ItemDto>,
  isLoading: boolean,
  error?: string,
  lastFetched?: number,
}
const initialState = {
  items: {} as PaginatedResponseDto<ItemDto>,
  isLoading: true,
  error: undefined as string | undefined,
  lastFetched: null as number | null,
}
export const itemSlice = createSlice({
  name: 'item',
  initialState,
  reducers: {
    getItems: (state) => {
      return state
    }
  },
  extraReducers: (builder) => {
    builder
    .addCase(loadItems.pending, (state) => {
      state.isLoading = true
    })
    .addCase(loadItems.fulfilled, (state, action) => {
      state.isLoading = false
      if (!action.payload.cached) {
        state.items = action.payload.data
        state.lastFetched = Date.now()
      }
      state.error = undefined
    })
    .addCase(loadItems.rejected, (state, action) => {
      console.error('Failed to fetch item:', action.error)
      state.isLoading = false
      state.error = action.error.message
    })
  }
})

export const { getItems } = itemSlice.actions

export default itemSlice.reducer
