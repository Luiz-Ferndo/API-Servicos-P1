package entity

// User representa um usuário do sistema
type User struct {
	ID                 uint       `gorm:"primaryKey;column:cd_user" json:"id"`
	Name               string     `gorm:"not null;column:nm_user;type:varchar(255)" json:"name"`
	Email              string     `gorm:"unique;not null;column:ds_email;type:varchar(255)" json:"email"`
	Password           string     `gorm:"not null;column:ds_password;type:varchar(255)" json:"-"`
	Roles              []*Role    `gorm:"many2many:user_role;foreignKey:ID;joinForeignKey:cd_user;References:ID;joinReferences:cd_role" json:"roles,omitempty"`
	Phones             []UserPhone `gorm:"foreignKey:UserID" json:"phones,omitempty"`
	ServicosOferecidos []*Servico `gorm:"many2many:prestador_servicos;foreignKey:ID;joinForeignKey:cd_user;References:ID;joinReferences:cd_servico" json:"servicosOferecidos,omitempty"`
}

// TableName especifica o nome da tabela
func (User) TableName() string {
	return "users"
}
