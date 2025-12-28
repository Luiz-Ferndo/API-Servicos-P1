package entity

// Servico representa um serviço oferecido no sistema
type Servico struct {
	ID         uint    `gorm:"primaryKey;column:cd_servico" json:"id"`
	Nome       string  `gorm:"not null;column:nm_servico;type:varchar(100)" json:"nome"`
	Valor      float64 `gorm:"not null;column:vl_servico;type:decimal(10,2)" json:"valor"`
	Descricao  string  `gorm:"not null;column:ds_servico;type:varchar(255)" json:"descricao"`
	Ativo      bool    `gorm:"not null;default:true;column:st_ativo" json:"ativo"`
	Prestadores []*User `gorm:"many2many:prestador_servicos;foreignKey:ID;joinForeignKey:cd_servico;References:ID;joinReferences:cd_user" json:"-"`
}

// TableName especifica o nome da tabela
func (Servico) TableName() string {
	return "servico"
}
