package types

// Connector : connector struct
type Connector struct {
	Name       string `json:"name"`
	Icon       string `json:"icon"`
	Adress     string `json:"adress"`
	IsRenderer bool   `json:"isRenderer"`
	IsSensor   bool   `json:"isSensor"`
	CanAnswer  bool   `json:"canAnswer"`
}
