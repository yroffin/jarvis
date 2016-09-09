package types

// DioResource : dio resource struct
type DioResource struct {
	Pin        int    `json:"pin"`
	Sender     int    `json:"sender"`
	Interuptor int    `json:"interruptor"`
	On         string `json:"on"`
}
