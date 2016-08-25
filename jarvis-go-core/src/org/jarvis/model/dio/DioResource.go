package devices

type DioResource struct {
	Pin        int  `json:"pin"`
	Sender     int  `json:"sender"`
	Interuptor int  `json:"interruptor"`
	On         bool `json:"on"`
}
