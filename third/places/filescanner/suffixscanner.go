package filescanner

import (
	"fmt"
	"os"
	"strings"
)

func scanDirEntry(dirEntry os.DirEntry, currentDir, suffix string) ([]string, error) {
	if dirEntry.IsDir() {
		return ScanForSuffix(currentDir+"/"+dirEntry.Name(), suffix)
	} else {
		if strings.HasSuffix(dirEntry.Name(), suffix) {
			return []string{currentDir + "/" + dirEntry.Name()}, nil
		} else {
			return nil, nil
		}
	}
}

func ScanForSuffix(targetDir, suffix string) ([]string, error) {
	var (
		targetDirFile *os.File
		err           error
		dirEntries    []os.DirEntry
		foundFiles    = make([]string, 0, 30)
	)
	targetDirFile, err = os.Open(targetDir)
	if err != nil {
		return nil, fmt.Errorf("scan %s for suffix %s: %w", targetDir, suffix, err)
	}
	dirEntries, err = targetDirFile.ReadDir(-1)
	if err != nil {
		return nil, fmt.Errorf("scan %s for suffix %s: %w", targetDir, suffix, err)
	}
	for _, v := range dirEntries {
		result, err := scanDirEntry(v, targetDir, suffix)
		if err != nil {
			return nil, fmt.Errorf("scan %s for suffix %s: %w", targetDir, suffix, err)
		}
		if len(result) == 0 {
			continue
		}
		foundFiles = append(foundFiles, result...)
	}
	return foundFiles, nil
}
