# Troubleshooting: ENOSPC (No Space Left on Device)

## The Error
```
npm error code ENOSPC
npm error syscall write
npm error errno -4055
npm error syscall write
npm error syscall ENOSPC: no space left on device, write
```

This means your system has run out of disk space. npm needs space to run the dev server and write to cache.

## Quick Fixes

### 1. Free Up Disk Space
- Empty your Recycle Bin
- Delete temporary files: `%TEMP%` folder
- Uninstall unused programs
- Move large files to cloud storage or external drive

### 2. Clear npm Cache (frees ~100–500MB)
```powershell
npm cache clean --force
```

### 3. Use a Different Temp Directory (if you have space on another drive)
If you have free space on drive `D:`:
```powershell
$env:TMP = "D:\temp"
$env:TEMP = "D:\temp"
cd frontend
npm run dev
```

### 4. Run After Freeing Space
Once you have freed disk space:
```powershell
cd frontend
npm run dev
```

Then open http://localhost:5173

---

**Note:** Framer Motion, React Icons, Chart.js, and Tailwind could not be installed due to disk space. The app uses:
- Tailwind CDN (in index.html)
- Axios, React Router (already installed)
- Custom CSS for animations and charts
