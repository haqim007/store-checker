package dev.haqim.pitjarusapp.domain.mechanism

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest


fun <T> Resource<T>.handle(
    onSuccess: () -> Unit,
    onError: () -> Unit = {},
    onLoading: () -> Unit = {},
    onIdle: () -> Unit = {},
    onAll: (res: Resource<T>) -> Unit = {}
){
    onAll.invoke(this)
    when (this) {
        is Resource.Success -> onSuccess.invoke()
        is Resource.Error -> onError.invoke()
        is Resource.Loading -> onLoading.invoke()
        else -> onIdle.invoke()
    }
}

suspend fun <T> Flow<Resource<T>>.handleCollect(
    onSuccess: (data: T?) -> Unit,
    onError: (message: String?, data: T?) -> Unit = { _: String?, _: T? -> },
    onLoading: (data: T?) -> Unit = {},
    onIdle: () -> Unit = {},
    onAll: (res: Resource<T>) -> Unit = {}
){
    this.collect{
        it.handle(
            { onSuccess(it.data) },
            { onError(it.message, it.data) },
            { onLoading(it.data) },
            onIdle,
            { res -> onAll(res) }
        )
    }
}

suspend fun <T> Flow<Resource<T>>.handleCollectLatest(
    onSuccess: () -> Unit,
    onError: () -> Unit = {},
    onLoading: () -> Unit = {},
    onIdle: () -> Unit = {}
){
    this.collectLatest{ it.handle(onSuccess, onError, onLoading, onIdle) }
}