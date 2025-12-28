package entity

import (
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/domain/enum"
)

// Role representa um papel (role) no sistema
type Role struct {
	ID          uint              `gorm:"primaryKey;column:cd_role" json:"id"`
	Name        enum.RoleName     `gorm:"unique;not null;column:nm_role;type:varchar(50)" json:"name"`
	Permissions []*Permission     `gorm:"many2many:role_permission;foreignKey:ID;joinForeignKey:cd_role;References:ID;joinReferences:permission_id" json:"permissions,omitempty"`
	Users       []*User           `gorm:"many2many:user_role;foreignKey:ID;joinForeignKey:cd_role;References:ID;joinReferences:cd_user" json:"-"`
}

// TableName especifica o nome da tabela
func (Role) TableName() string {
	return "role"
}
