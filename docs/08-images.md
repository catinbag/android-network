# Images: Glide and Picasso

## Idea

Loading a picture by hand needs a thread, bytes, decoding, a smaller size for the `ImageView`, memory, a cache and a cancel. A mistake gives `OutOfMemoryError` or a wrong picture in a row of a list. Glide and Picasso do all this in one chain.

## Roles

| Role | What it does | `glide` module | `picasso` module |
|---|---|---|---|
| Entry point | Binds the load to a lifecycle or an instance | `Glide.with(holder.image)` | `picasso` object from `Picasso.Builder` |
| Source | URL, file, asset | `load(url)` | `load(url)` |
| Placeholder | While loading | `placeholder(R.drawable.ic_dog_placeholder)` | same |
| Error image | On failure | `error(R.drawable.ic_broken_image)` | same |
| Size and crop | Fit the view | `centerCrop()` | `fit().centerCrop()` |
| Target | The view | `into(holder.image)` | `into(holder.image)` |

## How it works

Glide, `ImageAdapter.onBindViewHolder`:

```java
Glide.with(holder.image)
        .load(imageUrls.get(position))
        .placeholder(R.drawable.ic_dog_placeholder)
        .error(R.drawable.ic_broken_image)
        .centerCrop()
        .into(holder.image);
```

Picasso, the same place:

```java
picasso.load(imageUrls.get(position))
        .placeholder(R.drawable.ic_dog_placeholder)
        .error(R.drawable.ic_broken_image)
        .fit()
        .centerCrop()
        .into(holder.image);
```

Cache (Glide): memory, then disk, then network:

```
 load(url)
   ├─ memory cache?  yes → on the screen
   ├─ disk cache?    yes → decode → screen
   └─ network        → save to disk → decode → memory → screen
```

Rows of a `RecyclerView`: `into(view)` cancels the old load for this view. A row reused for position 15 does not show the photo of position 1.

`Glide.with(view)` ties the load to the lifecycle of the `Activity` or `Fragment`: when it is closed, the load stops. Use `with(holder.image)` or `with(fragment)`, not `Glide.with(applicationContext)` in an adapter.

## Glide and Picasso

| | Glide | Picasso |
|---|---|---|
| Network | Own code, not OkHttp | OkHttp (`OkHttp3Downloader`) |
| Shows in the `OkHttp` log | No | Yes, with the shared `OkHttpClient` |
| Cache of disk | Own | The cache of `OkHttpClient`; depends on the headers of the server |
| Clear the cache | `Glide.get(ctx).clearMemory()`, `clearDiskCache()` (not in the main thread) | No public method: `client.cache().evictAll()` and a new `Picasso` instance (`shutdown()` drops the memory cache) |
| Crop | `centerCrop()` | `fit().centerCrop()` |
| Version here | 4.16.0 (a 5.x line exists; not used) | 2.8 (see `versions.md`) |

## Screen (both modules)

- `Spinner` of breeds: `hound`, `husky`, `bulldog`, `unknown`. The list of URLs comes from Dog CEO through `RetrofitBreedRepository` (up to 120 photos). `unknown` gives HTTP 404 and the text of the server in the status line.
- `Local images`: 24 cells from six local PNG files (`file:///android_asset/images/N.png`). No network. This is the backup for a lecture without internet.
- `Broken URLs`: the host is replaced with a host that does not exist, so the cells show the error image.
- `Clear cache`: memory and disk.

## Status

The author built both modules and ran the checklist on an emulator: all steps were reported OK. Details that were not recorded separately: whether Picasso takes photos from the disk cache after a restart (it depends on the headers of `images.dog.ceo`), and the exact lines of the `OkHttp` log for photos.

## Advantages and disadvantages

Advantages:
- One chain instead of the code for the eight tasks.
- Cancel and cache are inside.

Disadvantages:
- Glide is outside of the OkHttp log and interceptors, so a custom interceptor (for example the offline mode) does not see the photos.
- A second library duplicates caches if an app uses both.
