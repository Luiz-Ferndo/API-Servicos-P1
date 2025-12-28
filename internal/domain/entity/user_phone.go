package entity

import (
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/domain/enum"
)

// UserPhone representa um telefone de usuário
type UserPhone struct {
	ID     uint           `gorm:"primaryKey;column:cd_phone" json:"id"`
	UserID uint           `gorm:"not null;column:cd_user" json:"userId"`
	Number string         `gorm:"not null;column:nr_phone;type:varchar(20)" json:"number"`
	Type   enum.PhoneType `gorm:"not null;column:tp_phone;type:varchar(20)" json:"type"`
	User   *User          `gorm:"foreignKey:UserID;references:ID" json:"-"`
}

// TableName especifica o nome da tabela
func (UserPhone) TableName() string {
	return "user_phone"
}
