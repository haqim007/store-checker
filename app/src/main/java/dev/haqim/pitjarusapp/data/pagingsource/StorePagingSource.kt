package dev.haqim.pitjarusapp.data.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.haqim.pitjarusapp.data.local.LocalDataSource
import dev.haqim.pitjarusapp.data.local.entity.toModel
import dev.haqim.pitjarusapp.domain.model.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

private const val STORE_STARTING_OFFSET_INDEX = 0

class StorePagingSource @Inject constructor(
    private val localDataSource: LocalDataSource
) : PagingSource<Int, Store>(){

    override fun getRefreshKey(state: PagingState<Int, Store>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1) ?:
            state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Store> {
        val position = params.key ?: STORE_STARTING_OFFSET_INDEX
        val offset = position * params.loadSize
        
        return try {
            val storesEntities = withContext(Dispatchers.IO){
                localDataSource.getStores(limit = params.loadSize, offset = offset)
            }
            val stores = storesEntities.toModel()
            
            val nextKey = if(stores.isEmpty()){
                null
            }else{
                position + 1
            }
            
            val prevKey = if(position == STORE_STARTING_OFFSET_INDEX) null else position - 1
            
            LoadResult.Page(
                data = stores,
                nextKey = nextKey,
                prevKey = prevKey
            )
        }catch (exception: IOException) {
            return LoadResult.Error(exception)
        }catch (e: Exception){
            return LoadResult.Error(e)
        }
    }
}