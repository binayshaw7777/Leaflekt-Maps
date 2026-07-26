const CACHE_NAME = 'leaflekt-tile-cache-v1';
const MAX_TILE_ENTRIES = 500;

self.addEventListener('install', (event) => {
    self.skipWaiting();
});

self.addEventListener('activate', (event) => {
    event.waitUntil(self.clients.claim());
});

self.addEventListener('fetch', (event) => {
    const url = event.request.url;
    if (url.match(/\.(png|jpg|jpeg|webp|pbf)(\?.*)?$/i) || url.includes('/tile') || url.includes('/tiles/')) {
        event.respondWith(
            caches.open(CACHE_NAME).then(async (cache) => {
                const response = await cache.match(event.request);
                if (response) return response;
                try {
                    const networkResponse = await fetch(event.request);
                    if (networkResponse && networkResponse.status === 200) {
                        const headers = new Headers(networkResponse.headers);
                        headers.set('sw-fetched-on', Date.now().toString());
                        const responseToCache = new Response(await networkResponse.clone().blob(), {
                            status: networkResponse.status,
                            statusText: networkResponse.statusText,
                            headers: headers
                        });
                        await cache.put(event.request, responseToCache);
                        pruneCacheLRU(cache);
                    }
                    return networkResponse;
                } catch (e) {
                    return response;
                }
            })
        );
    }
});

async function pruneCacheLRU(cache) {
    const keys = await cache.keys();
    if (keys.length <= MAX_TILE_ENTRIES) return;
    const items = await Promise.all(
        keys.map(async (req) => {
            const res = await cache.match(req);
            const time = res ? parseInt(res.headers.get('sw-fetched-on') || '0', 10) : 0;
            return { req, time };
        })
    );
    items.sort((a, b) => a.time - b.time);
    const toDelete = items.slice(0, items.length - MAX_TILE_ENTRIES);
    await Promise.all(toDelete.map((item) => cache.delete(item.req)));
}
